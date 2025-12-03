package tn.rnu.isi.mycycle.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import java.util.ArrayList;
import java.util.List;
import tn.rnu.isi.mycycle.models.User;
import tn.rnu.isi.mycycle.models.Period;
import tn.rnu.isi.mycycle.models.CycleEntry;
import tn.rnu.isi.mycycle.models.Symptom;
import tn.rnu.isi.mycycle.models.UserPreferences;
import tn.rnu.isi.mycycle.models.SymptomFrequency;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "mycycle.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PERIODS = "periods";
    private static final String TABLE_CYCLE_ENTRIES = "cycle_entries";
    private static final String TABLE_SYMPTOMS = "symptoms";
    private static final String TABLE_USER_PREFERENCES = "user_preferences";

    // Users table columns
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_CREATED_AT = "created_at";

    // Periods table columns
    private static final String COL_PERIOD_ID = "id";
    private static final String COL_PERIOD_USER_ID = "user_id";
    private static final String COL_PERIOD_START_DATE = "start_date";
    private static final String COL_PERIOD_END_DATE = "end_date";
    private static final String COL_PERIOD_DURATION = "duration";
    private static final String COL_PERIOD_FLOW = "flow"; // Light, Medium, Heavy

    // Cycle entries table columns
    private static final String COL_ENTRY_ID = "id";
    private static final String COL_ENTRY_USER_ID = "user_id";
    private static final String COL_ENTRY_DATE = "date";
    private static final String COL_ENTRY_PHASE = "phase"; // Menstrual, Follicular, Ovulation, Luteal
    private static final String COL_ENTRY_MOOD = "mood";
    private static final String COL_ENTRY_NOTES = "notes";

    // Symptoms table columns
    private static final String COL_SYMPTOM_ID = "id";
    private static final String COL_SYMPTOM_ENTRY_ID = "entry_id";
    private static final String COL_SYMPTOM_NAME = "name"; // Cramps, Headache, Fatigue, etc.
    private static final String COL_SYMPTOM_INTENSITY = "intensity"; // 0-10 scale

    // User preferences table columns
    private static final String COL_PREF_ID = "id";
    private static final String COL_PREF_USER_ID = "user_id";
    private static final String COL_PREF_CYCLE_LENGTH = "cycle_length";
    private static final String COL_PREF_PERIOD_LENGTH = "period_length";
    private static final String COL_PREF_LAST_PERIOD_START = "last_period_start";
    private static final String COL_PREF_TRACKED_SYMPTOMS = "tracked_symptoms"; // JSON or comma-separated
    private static final String COL_PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String COL_PREF_THEME = "theme"; // Light, Dark
    private static final String COL_PREF_LANGUAGE = "language";

    public DBHelper(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "(" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USER_NAME + " TEXT NOT NULL," +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL," +
                COL_USER_PASSWORD + " TEXT NOT NULL," +
                COL_USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(createUsersTable);

        // Create periods table
        String createPeriodsTable = "CREATE TABLE " + TABLE_PERIODS + "(" +
                COL_PERIOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PERIOD_USER_ID + " INTEGER NOT NULL," +
                COL_PERIOD_START_DATE + " TEXT NOT NULL," +
                COL_PERIOD_END_DATE + " TEXT," +
                COL_PERIOD_DURATION + " INTEGER," +
                COL_PERIOD_FLOW + " TEXT," +
                "FOREIGN KEY(" + COL_PERIOD_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(createPeriodsTable);

        // Create cycle_entries table
        String createCycleEntriesTable = "CREATE TABLE " + TABLE_CYCLE_ENTRIES + "(" +
                COL_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ENTRY_USER_ID + " INTEGER NOT NULL," +
                COL_ENTRY_DATE + " TEXT NOT NULL," +
                COL_ENTRY_PHASE + " TEXT," +
                COL_ENTRY_MOOD + " TEXT," +
                COL_ENTRY_NOTES + " TEXT," +
                "FOREIGN KEY(" + COL_ENTRY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE," +
                "UNIQUE(" + COL_ENTRY_USER_ID + ", " + COL_ENTRY_DATE + ")" +
                ")";
        db.execSQL(createCycleEntriesTable);

        // Create symptoms table
        String createSymptomsTable = "CREATE TABLE " + TABLE_SYMPTOMS + "(" +
                COL_SYMPTOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_SYMPTOM_ENTRY_ID + " INTEGER NOT NULL," +
                COL_SYMPTOM_NAME + " TEXT NOT NULL," +
                COL_SYMPTOM_INTENSITY + " INTEGER DEFAULT 0," +
                "FOREIGN KEY(" + COL_SYMPTOM_ENTRY_ID + ") REFERENCES " + TABLE_CYCLE_ENTRIES + "(" + COL_ENTRY_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(createSymptomsTable);

        // Create user_preferences table
        String createPreferencesTable = "CREATE TABLE " + TABLE_USER_PREFERENCES + "(" +
                COL_PREF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PREF_USER_ID + " INTEGER UNIQUE NOT NULL," +
                COL_PREF_CYCLE_LENGTH + " INTEGER DEFAULT 28," +
                COL_PREF_PERIOD_LENGTH + " INTEGER DEFAULT 5," +
                COL_PREF_LAST_PERIOD_START + " TEXT," +
                COL_PREF_TRACKED_SYMPTOMS + " TEXT," +
                COL_PREF_NOTIFICATIONS_ENABLED + " INTEGER DEFAULT 1," +
                COL_PREF_THEME + " TEXT DEFAULT 'Light'," +
                COL_PREF_LANGUAGE + " TEXT DEFAULT 'English'," +
                "FOREIGN KEY(" + COL_PREF_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(createPreferencesTable);

        // Create indexes for better performance
        db.execSQL("CREATE INDEX idx_periods_user_date ON " + TABLE_PERIODS + "(" + COL_PERIOD_USER_ID + ", " + COL_PERIOD_START_DATE + ")");
        db.execSQL("CREATE INDEX idx_entries_user_date ON " + TABLE_CYCLE_ENTRIES + "(" + COL_ENTRY_USER_ID + ", " + COL_ENTRY_DATE + ")");
        db.execSQL("CREATE INDEX idx_symptoms_entry ON " + TABLE_SYMPTOMS + "(" + COL_SYMPTOM_ENTRY_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYMPTOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CYCLE_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERIODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Insert a new user
     */
    public long insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        return userId;
    }

    /**
     * Check if user exists and return user ID
     */
    public long checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * Get user by ID
     */
    public User getUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_CREATED_AT};
        String selection = COL_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT))
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    /**
     * Update user information
     */
    public boolean updateUser(long userId, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        int rowsAffected = db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    // ==================== PERIOD OPERATIONS ====================

    /**
     * Insert a new period
     */
    public long insertPeriod(long userId, String startDate, String endDate, int duration, String flow) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PERIOD_USER_ID, userId);
        values.put(COL_PERIOD_START_DATE, startDate);
        values.put(COL_PERIOD_END_DATE, endDate);
        values.put(COL_PERIOD_DURATION, duration);
        values.put(COL_PERIOD_FLOW, flow);
        long periodId = db.insert(TABLE_PERIODS, null, values);
        db.close();
        return periodId;
    }

    /**
     * Get all periods for a user
     */
    public List<Period> getPeriods(long userId) {
        List<Period> periods = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_PERIOD_ID, COL_PERIOD_START_DATE, COL_PERIOD_END_DATE, COL_PERIOD_DURATION, COL_PERIOD_FLOW};
        String selection = COL_PERIOD_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COL_PERIOD_START_DATE + " DESC";
        Cursor cursor = db.query(TABLE_PERIODS, columns, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                Period period = new Period(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_PERIOD_ID)),
                        userId,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD_START_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD_END_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PERIOD_DURATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD_FLOW))
                );
                periods.add(period);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return periods;
    }

    /**
     * Get the most recent period for a user
     */
    public Period getLastPeriod(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_PERIOD_ID, COL_PERIOD_START_DATE, COL_PERIOD_END_DATE, COL_PERIOD_DURATION, COL_PERIOD_FLOW};
        String selection = COL_PERIOD_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COL_PERIOD_START_DATE + " DESC LIMIT 1";
        Cursor cursor = db.query(TABLE_PERIODS, columns, selection, selectionArgs, null, null, orderBy);
        Period period = null;
        if (cursor.moveToFirst()) {
            period = new Period(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_PERIOD_ID)),
                    userId,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD_START_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD_END_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PERIOD_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PERIOD_FLOW))
            );
        }
        cursor.close();
        db.close();
        return period;
    }

    // ==================== CYCLE ENTRY OPERATIONS ====================

    /**
     * Insert or update a cycle entry
     */
    public long insertOrUpdateCycleEntry(long userId, String date, String phase, String mood, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if entry exists
        String[] columns = {COL_ENTRY_ID};
        String selection = COL_ENTRY_USER_ID + " = ? AND " + COL_ENTRY_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(userId), date};
        Cursor cursor = db.query(TABLE_CYCLE_ENTRIES, columns, selection, selectionArgs, null, null, null);
        
        long entryId;
        if (cursor.moveToFirst()) {
            // Update existing entry
            entryId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ENTRY_ID));
            ContentValues values = new ContentValues();
            values.put(COL_ENTRY_PHASE, phase);
            values.put(COL_ENTRY_MOOD, mood);
            values.put(COL_ENTRY_NOTES, notes);
            db.update(TABLE_CYCLE_ENTRIES, values, COL_ENTRY_ID + " = ?", new String[]{String.valueOf(entryId)});
        } else {
            // Insert new entry
            ContentValues values = new ContentValues();
            values.put(COL_ENTRY_USER_ID, userId);
            values.put(COL_ENTRY_DATE, date);
            values.put(COL_ENTRY_PHASE, phase);
            values.put(COL_ENTRY_MOOD, mood);
            values.put(COL_ENTRY_NOTES, notes);
            entryId = db.insert(TABLE_CYCLE_ENTRIES, null, values);
        }
        cursor.close();
        db.close();
        return entryId;
    }

    /**
     * Get cycle entry for a specific date
     */
    public CycleEntry getCycleEntry(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ENTRY_ID, COL_ENTRY_PHASE, COL_ENTRY_MOOD, COL_ENTRY_NOTES};
        String selection = COL_ENTRY_USER_ID + " = ? AND " + COL_ENTRY_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(userId), date};
        Cursor cursor = db.query(TABLE_CYCLE_ENTRIES, columns, selection, selectionArgs, null, null, null);
        CycleEntry entry = null;
        if (cursor.moveToFirst()) {
            entry = new CycleEntry(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ENTRY_ID)),
                    userId,
                    date,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_PHASE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_MOOD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_NOTES))
            );
        }
        cursor.close();
        db.close();
        return entry;
    }

    /**
     * Get all cycle entries for a user within a date range
     */
    public List<CycleEntry> getCycleEntries(long userId, String startDate, String endDate) {
        List<CycleEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ENTRY_ID, COL_ENTRY_DATE, COL_ENTRY_PHASE, COL_ENTRY_MOOD, COL_ENTRY_NOTES};
        String selection = COL_ENTRY_USER_ID + " = ? AND " + COL_ENTRY_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {String.valueOf(userId), startDate, endDate};
        String orderBy = COL_ENTRY_DATE + " DESC";
        Cursor cursor = db.query(TABLE_CYCLE_ENTRIES, columns, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                CycleEntry entry = new CycleEntry(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ENTRY_ID)),
                        userId,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_PHASE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_MOOD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_NOTES))
                );
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    // ==================== SYMPTOM OPERATIONS ====================

    /**
     * Insert a symptom for a cycle entry
     */
    public long insertSymptom(long entryId, String name, int intensity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SYMPTOM_ENTRY_ID, entryId);
        values.put(COL_SYMPTOM_NAME, name);
        values.put(COL_SYMPTOM_INTENSITY, intensity);
        long symptomId = db.insert(TABLE_SYMPTOMS, null, values);
        db.close();
        return symptomId;
    }

    /**
     * Delete all symptoms for an entry (before updating)
     */
    public void deleteSymptomsForEntry(long entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SYMPTOMS, COL_SYMPTOM_ENTRY_ID + " = ?", new String[]{String.valueOf(entryId)});
        db.close();
    }

    /**
     * Get all symptoms for a cycle entry
     */
    public List<Symptom> getSymptomsForEntry(long entryId) {
        List<Symptom> symptoms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_SYMPTOM_ID, COL_SYMPTOM_NAME, COL_SYMPTOM_INTENSITY};
        String selection = COL_SYMPTOM_ENTRY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(entryId)};
        Cursor cursor = db.query(TABLE_SYMPTOMS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Symptom symptom = new Symptom(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_SYMPTOM_ID)),
                        entryId,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SYMPTOM_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SYMPTOM_INTENSITY))
                );
                symptoms.add(symptom);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return symptoms;
    }

    /**
     * Get symptom frequency statistics
     */
    public List<SymptomFrequency> getSymptomFrequency(long userId, String startDate, String endDate) {
        List<SymptomFrequency> frequencies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Join cycle_entries with symptoms to get symptom counts
        String query = "SELECT " + COL_SYMPTOM_NAME + ", COUNT(*) as count " +
                "FROM " + TABLE_SYMPTOMS + " s " +
                "INNER JOIN " + TABLE_CYCLE_ENTRIES + " e ON s." + COL_SYMPTOM_ENTRY_ID + " = e." + COL_ENTRY_ID + " " +
                "WHERE e." + COL_ENTRY_USER_ID + " = ? " +
                "AND e." + COL_ENTRY_DATE + " BETWEEN ? AND ? " +
                "GROUP BY " + COL_SYMPTOM_NAME + " " +
                "ORDER BY count DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        if (cursor.moveToFirst()) {
            do {
                SymptomFrequency freq = new SymptomFrequency(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SYMPTOM_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow("count"))
                );
                frequencies.add(freq);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return frequencies;
    }

    // ==================== USER PREFERENCES OPERATIONS ====================

    /**
     * Insert or update user preferences
     */
    public boolean insertOrUpdatePreferences(long userId, int cycleLength, int periodLength, String lastPeriodStart, String trackedSymptoms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PREF_USER_ID, userId);
        values.put(COL_PREF_CYCLE_LENGTH, cycleLength);
        values.put(COL_PREF_PERIOD_LENGTH, periodLength);
        values.put(COL_PREF_LAST_PERIOD_START, lastPeriodStart);
        values.put(COL_PREF_TRACKED_SYMPTOMS, trackedSymptoms);
        
        // Try to update first, if fails then insert
        int rowsAffected = db.update(TABLE_USER_PREFERENCES, values, COL_PREF_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        if (rowsAffected == 0) {
            db.insert(TABLE_USER_PREFERENCES, null, values);
        }
        db.close();
        return true;
    }

    /**
     * Get user preferences
     */
    public UserPreferences getUserPreferences(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COL_PREF_CYCLE_LENGTH, COL_PREF_PERIOD_LENGTH, COL_PREF_LAST_PERIOD_START,
                COL_PREF_TRACKED_SYMPTOMS, COL_PREF_NOTIFICATIONS_ENABLED, COL_PREF_THEME, COL_PREF_LANGUAGE
        };
        String selection = COL_PREF_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_USER_PREFERENCES, columns, selection, selectionArgs, null, null, null);
        UserPreferences prefs = null;
        if (cursor.moveToFirst()) {
            prefs = new UserPreferences(
                    userId,
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PREF_CYCLE_LENGTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PREF_PERIOD_LENGTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PREF_LAST_PERIOD_START)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PREF_TRACKED_SYMPTOMS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PREF_NOTIFICATIONS_ENABLED)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PREF_THEME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PREF_LANGUAGE))
            );
        }
        cursor.close();
        db.close();
        return prefs;
    }

    /**
     * Update notification settings
     */
    public boolean updateNotificationSettings(long userId, boolean enabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PREF_NOTIFICATIONS_ENABLED, enabled ? 1 : 0);
        int rowsAffected = db.update(TABLE_USER_PREFERENCES, values, COL_PREF_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Update theme
     */
    public boolean updateTheme(long userId, String theme) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PREF_THEME, theme);
        int rowsAffected = db.update(TABLE_USER_PREFERENCES, values, COL_PREF_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Update language
     */
    public boolean updateLanguage(long userId, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PREF_LANGUAGE, language);
        int rowsAffected = db.update(TABLE_USER_PREFERENCES, values, COL_PREF_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Calculate average cycle length
     */
    public double getAverageCycleLength(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT AVG(cycle_days) as avg_cycle " +
                "FROM (SELECT (julianday(" + COL_PERIOD_START_DATE + ") - julianday(LAG(" + COL_PERIOD_START_DATE + ") OVER (ORDER BY " + COL_PERIOD_START_DATE + "))) as cycle_days " +
                "FROM " + TABLE_PERIODS + " WHERE " + COL_PERIOD_USER_ID + " = ?) " +
                "WHERE cycle_days IS NOT NULL";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        double avgCycle = 28.0; // Default
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("avg_cycle");
            if (columnIndex >= 0 && !cursor.isNull(columnIndex)) {
                avgCycle = cursor.getDouble(columnIndex);
            }
        }
        cursor.close();
        db.close();
        return avgCycle;
    }

    // ==================== PREDICTION OPERATIONS ====================

    /**
     * Predict the next period start date based on last period and cycle length
     * @param userId User ID
     * @return Next period start date as String (yyyy-MM-dd) or null if insufficient data
     */
    public String predictNextPeriod(long userId) {
        UserPreferences prefs = getUserPreferences(userId);
        if (prefs == null || prefs.getLastPeriodStart() == null) {
            return null;
        }

        String lastPeriodStart = prefs.getLastPeriodStart();
        int cycleLength = prefs.getCycleLength();

        // Calculate next period: last period start + cycle length
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date lastPeriodDate = sdf.parse(lastPeriodStart);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(lastPeriodDate);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, cycleLength);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Predict the ovulation date based on cycle data
     * Ovulation typically occurs around day 14 of a 28-day cycle, or approximately cycle_length/2
     * @param userId User ID
     * @return Ovulation date as String (yyyy-MM-dd) or null if insufficient data
     */
    public String predictOvulationDate(long userId) {
        UserPreferences prefs = getUserPreferences(userId);
        if (prefs == null || prefs.getLastPeriodStart() == null) {
            return null;
        }

        String lastPeriodStart = prefs.getLastPeriodStart();
        int cycleLength = prefs.getCycleLength();

        // Ovulation typically occurs around day 14 of a 28-day cycle
        // For other cycle lengths, it's approximately cycle_length/2 days after period start
        // Standard calculation: ovulation = period start + (cycle_length - 14) days
        // This assumes luteal phase is typically 14 days
        int daysToOvulation = cycleLength - 14; // Days from period start to ovulation

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date lastPeriodDate = sdf.parse(lastPeriodStart);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(lastPeriodDate);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, daysToOvulation);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Predict the fertile window (5 days before ovulation to ovulation day)
     * The fertile window is typically 5 days before ovulation through ovulation day
     * @param userId User ID
     * @return Array of two strings: [startDate, endDate] or null if insufficient data
     *         startDate: First day of fertile window
     *         endDate: Last day of fertile window (ovulation day)
     */
    public String[] predictFertileWindow(long userId) {
        String ovulationDate = predictOvulationDate(userId);
        if (ovulationDate == null) {
            return null;
        }

        // Fertile window: 5 days before ovulation to ovulation day (6 days total)
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date ovulation = sdf.parse(ovulationDate);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(ovulation);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -5); // 5 days before ovulation
            String fertileStart = sdf.format(calendar.getTime());
            String fertileEnd = ovulationDate; // Ovulation day is the last day

            return new String[]{fertileStart, fertileEnd};
        } catch (Exception e) {
            return null;
        }
    }
}
