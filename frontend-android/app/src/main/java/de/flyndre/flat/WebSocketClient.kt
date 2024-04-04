package de.flyndre.flat

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class WebSocketClient {
    private lateinit var webSocket: okhttp3.WebSocket
    private var socketListener: SocketListener? = null
    private var socketUrl = ""
    private var shouldReconnect = true
    private var client: OkHttpClient? = null

    //set self sign certificate
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(
            chain: Array<out X509Certificate>?,
            authType: String?
        ) {}

        override fun checkServerTrusted(
            chain: Array<out X509Certificate>?,
            authType: String?
        ) {}

        override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
    })

    val sslContext = SSLContext.getInstance("SSL")


    companion object {
        private lateinit var instance: WebSocketClient
        @JvmStatic
        @Synchronized
        //This function gives singleton instance of WebSocket.
        fun getInstance(): WebSocketClient {
            synchronized(WebSocketClient::class) {
                if (!::instance.isInitialized) {
                    instance = WebSocketClient()
                }
            }
            return instance
        }
    }

    fun setListener(listener: SocketListener) {
        this.socketListener = listener
    }

    fun setSocketUrl(socketUrl: String) {
        this.socketUrl = socketUrl
    }

    private fun initWebSocket() {
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        client = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier{ _, _ -> true }
            .build()
        val request = Request.Builder().url(url = socketUrl).build()
        webSocket = client!!.newWebSocket(request, webSocketListener)
        //this must me done else memory leak will be caused
        client!!.dispatcher.executorService.shutdown()
    }

    fun connect() {
        shouldReconnect = true
        initWebSocket()
    }

    fun reconnect() {
        initWebSocket()
    }

    //send
    fun sendMessage(message: String) {
        if (::webSocket.isInitialized) webSocket.send(message)
    }


    //We can close socket by two way:

    //1. websocket.webSocket.close(1000, "Dont need connection")
    //This attempts to initiate a graceful shutdown of this web socket.
    //Any already-enqueued messages will be transmitted before the close message is sent but
    //subsequent calls to send will return false and their messages will not be enqueued.

    //2. websocket.cancel()
    //This immediately and violently release resources held by this web socket,
    //discarding any enqueued messages.

    //Both does nothing if the web socket has already been closed or canceled.
    fun disconnect() {
        if (::webSocket.isInitialized) webSocket.close(1000, "Do not need connection anymore.")
        shouldReconnect = false
    }

    interface SocketListener {
        fun onMessage(message: String)
    }


    private val webSocketListener = object : WebSocketListener() {
        //called when connection succeeded
        //we are sending a message just after the socket is opened
        override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
        }

        //called when text message received
        override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
            socketListener?.onMessage(text)
        }

        //called when binary message received
        override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        }

        override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            //called when no more messages and the connection should be released
            if (shouldReconnect) reconnect()
        }

        override fun onFailure(
            webSocket: okhttp3.WebSocket, t: Throwable, response: Response?
        ) {
            if (shouldReconnect) reconnect()
        }
    }
}