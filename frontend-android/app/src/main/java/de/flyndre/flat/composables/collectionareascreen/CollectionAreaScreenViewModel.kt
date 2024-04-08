package de.flyndre.flat.composables.collectionareascreen

import androidx.lifecycle.ViewModel
import de.flyndre.flat.database.AppDatabase

class CollectionAreaScreenViewModel(presetId: Long, db: AppDatabase): ViewModel() {
    //appdatabase
    private var _db: AppDatabase
    //preset id
    private var _presetId: Long

    init{
        _db = db
        _presetId = presetId
    }
}