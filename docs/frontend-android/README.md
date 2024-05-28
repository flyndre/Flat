# Android Frontend

## Deploy
### APK
To build an apk open the android project in Android Studio and use the build menu to build your apk.
### Assetlink
To support the feature to open a join link directly in the app you have to create a [assetlinks.json](./frontend-android/assetlinks.json) file for your domain.
This file must than be reachabel under your domain like [https://flat.buhss.de/.well-known/assetlinks.json](https://flat.buhss.de/.well-known/assetlinks.json).
More information on that [here](https://developer.android.com/studio/write/app-link-indexing)