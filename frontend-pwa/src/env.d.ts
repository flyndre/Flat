interface ViteTypeOptions {
  strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  readonly MODE: 'demo' | 'prod'
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
