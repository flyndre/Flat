# Client Storage

Because draft and completed collections are only stored on the client, a capable client-side storage is needed.
Browsers provide the so called IndexedDB, a client-side NoSQL database that allows storage of objects way greater than solutions like LocalStorage or similar.

To make IndexedDB more comfortable to work with, we use [Dexie.js](https://dexie.org/), which is a wrapper library for IndexedDB.
Dexie provides [Guide for using it with Vue](https://dexie.org/docs/Tutorial/Vue).
As [described there](https://dexie.org/docs/Tutorial/Vue#5-create-a-component-that-queries-data), we wrap references to `DexieTable`s in a `useObservable` from VueUse and a `liveQuery` from Dexie to get a reactive data reference.

## Structure

It is common to create a single Dexie instance in `src/database/db.ts` which is exported.
Tables inside IndexedDB are defined in that file according to the Dexie with Vue docs (see above).
We then use the exported `db` instance in one file per table inside `src/database/` where we wrap a `liveQuery` reference wrapped inside a `useObservable` to get a reactive `Ref` of the table.

For instance, the `collections` table is defined in `src/database/db.ts`.
A reactive reference is exported in `src/database/collections.ts`, which can be used in components, to access the data reactively.

To mutate the data, we export the Table as `<tablename>Service` from the table's file.
