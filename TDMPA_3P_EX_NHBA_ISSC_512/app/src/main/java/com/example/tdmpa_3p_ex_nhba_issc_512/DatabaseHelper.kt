package com.example.tdmpa_3p_ex_nhba_issc_512

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        const val DATABASE_NAME = "AgendaDB"
        const val DATABASE_VERSION = 1

        // EventTable
        const val TABLE_EVENT = "EventTable"
        const val EVENT_ID = "_id"
        const val EVENT_NAME = "name"
        const val EVENT_REMINDER = "reminder"
        const val EVENT_STATE = "state"

        // NoteTable
        const val TABLE_NOTE = "NoteTable"
        const val NOTE_ID = "_id"
        const val NOTE_NAME = "name"
        const val NOTE_REMINDER = "reminder"
        const val NOTE_STATE = "state"

        // TaskTable
        const val TABLE_TASK = "TaskTable"
        const val TASK_ID = "_id"
        const val TASK_NAME = "name"
        const val TASK_REMINDER = "reminder"
        const val TASK_STATE = "state"

        // Complete
        const val TABLE_COMPLETED_ITEMS = "CompletedItems"
        const val COMPLETED_ID = "_id"
        const val COMPLETED_TYPE = "type"
        const val COMPLETED_NAME = "name"
        const val COMPLETED_REMINDER = "reminder"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createEventTable = ("CREATE TABLE $TABLE_EVENT ($EVENT_ID INTEGER PRIMARY KEY, $EVENT_NAME TEXT, $EVENT_REMINDER DATETIME, $EVENT_STATE BOOLEAN);")
        val createNoteTable = ("CREATE TABLE $TABLE_NOTE ($NOTE_ID INTEGER PRIMARY KEY, $NOTE_NAME TEXT, $NOTE_REMINDER DATETIME, $NOTE_STATE BOOLEAN);")
        val createTaskTable = ("CREATE TABLE $TABLE_TASK ($TASK_ID INTEGER PRIMARY KEY, $TASK_NAME TEXT, $TASK_REMINDER DATETIME, $TASK_STATE BOOLEAN);")
        val createCompletedItemsTable = ("CREATE TABLE $TABLE_COMPLETED_ITEMS ($COMPLETED_ID INTEGER PRIMARY KEY, $COMPLETED_TYPE TEXT, $COMPLETED_NAME TEXT, $COMPLETED_REMINDER DATETIME);")
        db.execSQL(createCompletedItemsTable)
        db.execSQL(createEventTable)
        db.execSQL(createNoteTable)
        db.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASK")
        onCreate(db)
    }

    // Método para agregar un evento
    fun addEvent(eventModel: EventModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(EVENT_NAME, eventModel.name)
        values.put(EVENT_REMINDER, eventModel.reminder)
        values.put(EVENT_STATE, eventModel.state)
        db.insert(TABLE_EVENT, null, values)
        db.close()
    }

    // Método para obtener un evento por su ID
    @SuppressLint("Range")
    fun getEventById(id: Int): EventModel? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_EVENT,
            arrayOf(EVENT_ID, EVENT_NAME, EVENT_REMINDER, EVENT_STATE),
            "$EVENT_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var eventModel: EventModel? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(EVENT_NAME))
            val reminder = cursor.getString(cursor.getColumnIndex(EVENT_REMINDER))
            val state = cursor.getInt(cursor.getColumnIndex(EVENT_STATE)) > 0
            eventModel = EventModel(id, name, reminder, state)
        }
        cursor.close()
        db.close()
        return eventModel
    }

    // Método para obtener todos los eventos
    @SuppressLint("Range")
    fun getAllEvents(): List<EventModel> {
        val eventsList = mutableListOf<EventModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_EVENT"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(EVENT_ID))
                val name = cursor.getString(cursor.getColumnIndex(EVENT_NAME))
                val reminder = cursor.getString(cursor.getColumnIndex(EVENT_REMINDER))
                val state = cursor.getInt(cursor.getColumnIndex(EVENT_STATE)) > 0 // Convert int to boolean
                val event = EventModel(id, name, reminder, state)
                eventsList.add(event)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return eventsList
    }


    // Método para actualizar un evento
    fun updateEvent(eventModel: EventModel?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(EVENT_NAME, eventModel?.name)
        values.put(EVENT_REMINDER, eventModel?.reminder)
        values.put(EVENT_STATE, eventModel?.state)
        db.update(TABLE_EVENT, values, "$EVENT_ID=?", arrayOf(eventModel?.id.toString()))
        db.close()
    }

    // Método para eliminar un evento
    fun deleteEvent(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_EVENT, "$EVENT_ID=?", arrayOf(id.toString()))
        db.close()
    }

    // Método para agregar una nota
    fun addNote(noteModel: NoteModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOTE_NAME, noteModel.name)
        values.put(NOTE_REMINDER, noteModel.reminder)
        values.put(NOTE_STATE, noteModel.state)
        db.insert(TABLE_NOTE, null, values)
        db.close()
    }


    @SuppressLint("Range")
    fun getAllNotes(): List<NoteModel> {
        val notesList = mutableListOf<NoteModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NOTE"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(NOTE_ID))
                val name = cursor.getString(cursor.getColumnIndex(NOTE_NAME))
                val reminder = cursor.getString(cursor.getColumnIndex(NOTE_REMINDER))
                val state = cursor.getInt(cursor.getColumnIndex(NOTE_STATE)) > 0 // Convert int to boolean
                val note = NoteModel(id, name, reminder, state)
                notesList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notesList
    }

    // Método para obtener una nota por su ID
    @SuppressLint("Range")
    fun getNoteById(id: Int): NoteModel? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_EVENT,
            arrayOf(EVENT_ID, EVENT_NAME, EVENT_REMINDER, EVENT_STATE),
            "$EVENT_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var noteModel: NoteModel? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(EVENT_NAME))
            val reminder = cursor.getString(cursor.getColumnIndex(EVENT_REMINDER))
            val state = cursor.getInt(cursor.getColumnIndex(EVENT_STATE)) > 0
            noteModel = NoteModel(id, name, reminder, state)
        }
        cursor.close()
        db.close()
        return noteModel
    }

    // Método para actualizar una nota
    fun updateNote(noteModel: NoteModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOTE_NAME, noteModel.name)
        values.put(NOTE_REMINDER, noteModel.reminder)
        values.put(NOTE_STATE, noteModel.state)
        db.update(TABLE_NOTE, values, "$NOTE_ID=?", arrayOf(noteModel.id.toString()))
        db.close()
    }

    // Método para eliminar una nota
    fun deleteNote(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTE, "$NOTE_ID=?", arrayOf(id.toString()))
        db.close()
    }

    // Método para agregar una tarea
    fun addTask(taskModel: TaskModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK_NAME, taskModel.name)
        values.put(TASK_REMINDER, taskModel.reminder)
        values.put(TASK_STATE, taskModel.state)
        db.insert(TABLE_TASK, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllTasks(): List<TaskModel> {
        val tasksList = mutableListOf<TaskModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_TASK"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(TASK_ID))
                val name = cursor.getString(cursor.getColumnIndex(TASK_NAME))
                val reminder = cursor.getString(cursor.getColumnIndex(TASK_REMINDER))
                val state = cursor.getInt(cursor.getColumnIndex(TASK_STATE)) > 0 // Convert int to boolean
                val task = TaskModel(id, name, reminder, state)
                tasksList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tasksList
    }

    // Método para obtener una tarea por su ID
    @SuppressLint("Range")
    fun getTaskById(id: Int): TaskModel? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TASK,
            arrayOf(TASK_ID, TASK_NAME, TASK_REMINDER, TASK_STATE),
            "$TASK_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var taskModel: TaskModel? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(TASK_NAME))
            val reminder = cursor.getString(cursor.getColumnIndex(TASK_REMINDER))
            val state = cursor.getInt(cursor.getColumnIndex(TASK_STATE)) > 0
            taskModel = TaskModel(id, name, reminder, state)
        }
        cursor.close()
        db.close()
        return taskModel
    }

    // Método para actualizar una tarea
    fun updateTask(taskModel: TaskModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK_NAME, taskModel.name)
        values.put(TASK_REMINDER, taskModel.reminder)
        values.put(TASK_STATE, taskModel.state)
        db.update(TABLE_TASK, values, "$TASK_ID=?", arrayOf(taskModel.id.toString()))
        db.close()
    }

    // Método para eliminar una tarea
    fun deleteTask(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASK, "$TASK_ID=?", arrayOf(id.toString()))
        db.close()
    }

    // Método para agregar un ítem completado
    fun addCompletedItem(completedItem: CompletedItemModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COMPLETED_TYPE, completedItem.type)
        values.put(COMPLETED_NAME, completedItem.name)
        values.put(COMPLETED_REMINDER, completedItem.reminder)
        db.insert(TABLE_COMPLETED_ITEMS, null, values)
        db.close()
    }

    // Método para obtener todos los ítems completados
    @SuppressLint("Range")
    fun getAllCompletedItems(): List<CompletedItemModel> {
        val itemList = ArrayList<CompletedItemModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COMPLETED_ITEMS", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COMPLETED_ID))
                val type = cursor.getString(cursor.getColumnIndex(COMPLETED_TYPE))
                val name = cursor.getString(cursor.getColumnIndex(COMPLETED_NAME))
                val reminder = cursor.getString(cursor.getColumnIndex(COMPLETED_REMINDER))
                itemList.add(CompletedItemModel(id, type, name, reminder))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return itemList
    }


}

data class EventModel(val id: Int, var name: String, var reminder: String, var state: Boolean)
data class NoteModel(val id: Int, var name: String, var reminder: String, var state: Boolean)
data class TaskModel(val id: Int, var name: String, var reminder: String, var state: Boolean)
data class CompletedItemModel(val id: Int, val type: String, val name: String, val reminder: String)

