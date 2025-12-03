package tn.rnu.isi.mycycle.database;

import android.content.Context;
import java.util.List;
import tn.rnu.isi.mycycle.models.User;
import tn.rnu.isi.mycycle.models.Period;
import tn.rnu.isi.mycycle.models.CycleEntry;
import tn.rnu.isi.mycycle.models.Symptom;
import tn.rnu.isi.mycycle.models.UserPreferences;
import tn.rnu.isi.mycycle.models.SymptomFrequency;
import tn.rnu.isi.mycycle.utils.DateUtils;

/**
 * DatabaseManager provides a high-level interface for database operations.
 * This class acts as a repository pattern, making it easier to work with the database.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private DBHelper dbHelper;
    private Context context;

    private DatabaseManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DBHelper(this.context);
    }

    /**
     * Get singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Register a new user
     * @return User ID if successful, -1 if email already exists
     */
    public long registerUser(String name, String email, String password) {
        if (dbHelper.emailExists(email)) {
            return -1; // Email already exists
        }
        return dbHelper.insertUser(name, email, password);
    }

    /**
     * Login user
     * @return User ID if credentials are correct, -1 otherwise
     */
    public long loginUser(String email, String password) {
        return dbHelper.checkUser(email, password);
    }

    /**
     * Get user by ID
     */
    public User getUser(long userId) {
        return dbHelper.getUser(userId);
    }

    /**
     * Update user profile
     */
    public boolean updateUserProfile(long userId, String name, String email) {
        return dbHelper.updateUser(userId, name, email);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return dbHelper.emailExists(email);
    }

    // ==================== PERIOD OPERATIONS ====================

    /**
     * Add a new period
     */
    public long addPeriod(long userId, String startDate, String endDate, int duration, String flow) {
        return dbHelper.insertPeriod(userId, startDate, endDate, duration, flow);
    }

    /**
     * Get all periods for a user
     */
    public List<Period> getPeriods(long userId) {
        return dbHelper.getPeriods(userId);
    }

    /**
     * Get the most recent period
     */
    public Period getLastPeriod(long userId) {
        return dbHelper.getLastPeriod(userId);
    }

    // ==================== CYCLE ENTRY OPERATIONS ====================

    /**
     * Save or update a daily cycle entry (symptoms, mood, notes)
     */
    public long saveCycleEntry(long userId, String date, String phase, String mood, String notes,
                               List<Symptom> symptoms) {
        // Insert or update the cycle entry
        long entryId = dbHelper.insertOrUpdateCycleEntry(userId, date, phase, mood, notes);
        
        // Delete existing symptoms and add new ones
        if (entryId > 0 && symptoms != null && !symptoms.isEmpty()) {
            dbHelper.deleteSymptomsForEntry(entryId);
            for (Symptom symptom : symptoms) {
                dbHelper.insertSymptom(entryId, symptom.getName(), symptom.getIntensity());
            }
        }
        
        return entryId;
    }

    /**
     * Get cycle entry for a specific date
     */
    public CycleEntry getCycleEntry(long userId, String date) {
        CycleEntry entry = dbHelper.getCycleEntry(userId, date);
        if (entry != null) {
            // Load symptoms for this entry
            List<Symptom> symptoms = dbHelper.getSymptomsForEntry(entry.getId());
            entry.setSymptoms(symptoms);
        }
        return entry;
    }

    /**
     * Get cycle entries within a date range
     */
    public List<CycleEntry> getCycleEntries(long userId, String startDate, String endDate) {
        List<CycleEntry> entries = dbHelper.getCycleEntries(userId, startDate, endDate);
        // Load symptoms for each entry
        for (CycleEntry entry : entries) {
            List<Symptom> symptoms = dbHelper.getSymptomsForEntry(entry.getId());
            entry.setSymptoms(symptoms);
        }
        return entries;
    }

    // ==================== SYMPTOM OPERATIONS ====================

    /**
     * Get symptom frequency statistics
     */
    public List<SymptomFrequency> getSymptomFrequency(long userId, String startDate, String endDate) {
        return dbHelper.getSymptomFrequency(userId, startDate, endDate);
    }

    // ==================== USER PREFERENCES OPERATIONS ====================

    /**
     * Save user preferences (cycle setup)
     */
    public boolean saveUserPreferences(long userId, int cycleLength, int periodLength,
                                      String lastPeriodStart, String trackedSymptoms) {
        return dbHelper.insertOrUpdatePreferences(userId, cycleLength, periodLength,
                lastPeriodStart, trackedSymptoms);
    }

    /**
     * Get user preferences
     */
    public UserPreferences getUserPreferences(long userId) {
        return dbHelper.getUserPreferences(userId);
    }

    /**
     * Update notification settings
     */
    public boolean updateNotificationSettings(long userId, boolean enabled) {
        return dbHelper.updateNotificationSettings(userId, enabled);
    }

    /**
     * Update theme preference
     */
    public boolean updateTheme(long userId, String theme) {
        return dbHelper.updateTheme(userId, theme);
    }

    /**
     * Update language preference
     */
    public boolean updateLanguage(long userId, String language) {
        return dbHelper.updateLanguage(userId, language);
    }

    // ==================== STATISTICS OPERATIONS ====================

    /**
     * Get average cycle length
     */
    public double getAverageCycleLength(long userId) {
        return dbHelper.getAverageCycleLength(userId);
    }

    /**
     * Predict the next period start date
     * @return Next period date as String (yyyy-MM-dd) or null
     */
    public String predictNextPeriod(long userId) {
        return dbHelper.predictNextPeriod(userId);
    }

    /**
     * Predict the ovulation date
     * @return Ovulation date as String (yyyy-MM-dd) or null
     */
    public String predictOvulationDate(long userId) {
        return dbHelper.predictOvulationDate(userId);
    }

    /**
     * Predict the fertile window
     * @return Array with [startDate, endDate] or null
     */
    public String[] predictFertileWindow(long userId) {
        return dbHelper.predictFertileWindow(userId);
    }

    /**
     * Calculate next period date based on last period and cycle length
     * @deprecated Use predictNextPeriod() instead
     */
    @Deprecated
    public String calculateNextPeriodDate(long userId) {
        return predictNextPeriod(userId);
    }

    /**
     * Calculate days until next period
     */
    public int getDaysUntilNextPeriod(long userId) {
        String nextPeriodDate = predictNextPeriod(userId);
        if (nextPeriodDate == null) {
            return -1;
        }
        
        String today = DateUtils.getCurrentDate();
        return DateUtils.daysBetween(today, nextPeriodDate);
    }

    /**
     * Determine current cycle phase
     */
    public String getCurrentPhase(long userId) {
        UserPreferences prefs = getUserPreferences(userId);
        if (prefs == null || prefs.getLastPeriodStart() == null) {
            return "Unknown";
        }
        
        String lastPeriodStart = prefs.getLastPeriodStart();
        int cycleLength = prefs.getCycleLength();
        int periodLength = prefs.getPeriodLength();
        String today = DateUtils.getCurrentDate();
        
        int daysSincePeriodStart = DateUtils.daysBetween(lastPeriodStart, today);
        
        // Menstrual phase: Days 1-5 (or period length)
        if (daysSincePeriodStart >= 0 && daysSincePeriodStart < periodLength) {
            return "Menstrual";
        }
        // Follicular phase: After period until ovulation (typically days 6-13)
        else if (daysSincePeriodStart >= periodLength && daysSincePeriodStart < (cycleLength - 14)) {
            return "Follicular";
        }
        // Ovulation phase: Around day 14 (or cycle_length - 14 days after period start)
        else if (daysSincePeriodStart >= (cycleLength - 15) && daysSincePeriodStart <= (cycleLength - 13)) {
            return "Ovulation";
        }
        // Luteal phase: After ovulation until next period (typically days 15-28)
        else if (daysSincePeriodStart > (cycleLength - 13) && daysSincePeriodStart < cycleLength) {
            return "Luteal";
        }
        // If beyond cycle length, we're in the next cycle
        else {
            return "Follicular"; // Default to follicular for next cycle
        }
    }
}

