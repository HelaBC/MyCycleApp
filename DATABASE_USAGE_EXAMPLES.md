# MyCycle Database Usage Examples

This document provides examples of how to use the SQLite database in the MyCycle app.

## 📚 Database Structure

The database consists of 5 main tables:
1. **users** - User accounts
2. **periods** - Period tracking
3. **cycle_entries** - Daily cycle entries (mood, notes, phase)
4. **symptoms** - Symptom tracking with intensity
5. **user_preferences** - User settings and cycle configuration

## 🚀 Getting Started

### Initialize DatabaseManager

```java
// In your Activity or Application class
DatabaseManager dbManager = DatabaseManager.getInstance(this);
```

## 📝 Usage Examples

### 1. User Registration

```java
// Register a new user
long userId = dbManager.registerUser("Jane Doe", "jane@example.com", "password123");

if (userId > 0) {
    // Registration successful
    // Save userId to SharedPreferences for session management
} else {
    // Email already exists
    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
}
```

### 2. User Login

```java
// Login user
long userId = dbManager.loginUser("jane@example.com", "password123");

if (userId > 0) {
    // Login successful
    // Save userId to SharedPreferences
    User user = dbManager.getUser(userId);
    // Navigate to HomeActivity
} else {
    // Invalid credentials
    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
}
```

### 3. Save User Preferences (Sign Up Step 2)

```java
// Save cycle setup information
long userId = getCurrentUserId(); // Get from SharedPreferences

String lastPeriodStart = "2025-01-15"; // Format: yyyy-MM-dd
int cycleLength = 28;
int periodLength = 5;
String trackedSymptoms = "Cramps,Headache,Fatigue,Bloating"; // Comma-separated

boolean success = dbManager.saveUserPreferences(
    userId, 
    cycleLength, 
    periodLength, 
    lastPeriodStart, 
    trackedSymptoms
);

if (success) {
    // Also add the period to periods table
    String endDate = DateUtils.addDays(lastPeriodStart, periodLength);
    dbManager.addPeriod(userId, lastPeriodStart, endDate, periodLength, "Medium");
}
```

### 4. Save Daily Symptoms

```java
// Save symptoms for today
long userId = getCurrentUserId();
String today = DateUtils.getCurrentDate(); // "2025-01-20"

// Create symptom list
List<Symptom> symptoms = new ArrayList<>();
symptoms.add(new Symptom(0, 0, "Cramps", 5));
symptoms.add(new Symptom(0, 0, "Fatigue", 3));
symptoms.add(new Symptom(0, 0, "Bloating", 7));

// Determine phase (you'll need to implement this logic)
String phase = calculatePhase(userId, today); // "Follicular", "Ovulation", etc.
String mood = "😊"; // From mood selector
String notes = "Feeling good today";

long entryId = dbManager.saveCycleEntry(userId, today, phase, mood, notes, symptoms);

if (entryId > 0) {
    Toast.makeText(this, "Symptoms saved successfully", Toast.LENGTH_SHORT).show();
}
```

### 5. Load Cycle Entry for a Date

```java
// Get entry for a specific date
long userId = getCurrentUserId();
String date = "2025-01-20";

CycleEntry entry = dbManager.getCycleEntry(userId, date);

if (entry != null) {
    // Entry exists - populate UI
    String mood = entry.getMood();
    String notes = entry.getNotes();
    List<Symptom> symptoms = entry.getSymptoms();
    
    // Update UI with data
} else {
    // No entry for this date - show empty form
}
```

### 6. Get Period Information

```java
// Get last period
long userId = getCurrentUserId();
Period lastPeriod = dbManager.getLastPeriod(userId);

if (lastPeriod != null) {
    String startDate = lastPeriod.getStartDate();
    int duration = lastPeriod.getDuration();
    // Calculate next period date
    UserPreferences prefs = dbManager.getUserPreferences(userId);
    int cycleLength = prefs.getCycleLength();
    String nextPeriodDate = DateUtils.addDays(startDate, cycleLength);
}

// Get all periods
List<Period> allPeriods = dbManager.getPeriods(userId);
```

### 7. Calculate Next Period

```java
// Calculate days until next period
long userId = getCurrentUserId();
Period lastPeriod = dbManager.getLastPeriod(userId);

if (lastPeriod != null) {
    UserPreferences prefs = dbManager.getUserPreferences(userId);
    int cycleLength = prefs.getCycleLength();
    
    String nextPeriodDate = DateUtils.addDays(lastPeriod.getStartDate(), cycleLength);
    String today = DateUtils.getCurrentDate();
    int daysUntil = DateUtils.daysBetween(today, nextPeriodDate);
    
    // Display: "Next Period in X days"
}
```

### 8. Get Statistics

```java
// Get symptom frequency for last 3 months
long userId = getCurrentUserId();
String endDate = DateUtils.getCurrentDate();
String startDate = DateUtils.addDays(endDate, -90); // 3 months ago

List<SymptomFrequency> frequencies = dbManager.getSymptomFrequency(userId, startDate, endDate);

for (SymptomFrequency freq : frequencies) {
    String symptomName = freq.getSymptomName();
    int count = freq.getCount();
    // Display in statistics screen
}

// Get average cycle length
double avgCycleLength = dbManager.getAverageCycleLength(userId);
```

### 9. Update Settings

```java
// Update theme
long userId = getCurrentUserId();
dbManager.updateTheme(userId, "Dark");

// Update language
dbManager.updateLanguage(userId, "French");

// Update notifications
dbManager.updateNotificationSettings(userId, true);
```

### 10. Get User Preferences

```java
// Load user preferences
long userId = getCurrentUserId();
UserPreferences prefs = dbManager.getUserPreferences(userId);

if (prefs != null) {
    int cycleLength = prefs.getCycleLength();
    int periodLength = prefs.getPeriodLength();
    String lastPeriodStart = prefs.getLastPeriodStart();
    String trackedSymptoms = prefs.getTrackedSymptoms();
    boolean notificationsEnabled = prefs.isNotificationsEnabled();
    String theme = prefs.getTheme();
    String language = prefs.getLanguage();
}
```

## 🔄 Complete Example: SymptomsActivity

```java
public class SymptomsActivity extends AppCompatActivity {
    private DatabaseManager dbManager;
    private long userId;
    private String selectedDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);
        
        dbManager = DatabaseManager.getInstance(this);
        userId = getCurrentUserId(); // From SharedPreferences
        selectedDate = DateUtils.getCurrentDate(); // Default to today
        
        // Load existing entry if exists
        loadEntryForDate(selectedDate);
    }
    
    private void loadEntryForDate(String date) {
        CycleEntry entry = dbManager.getCycleEntry(userId, date);
        
        if (entry != null) {
            // Populate UI with existing data
            // Set mood selector
            // Set symptom sliders
            // Set notes
        }
    }
    
    private void saveEntry() {
        // Get values from UI
        String mood = getSelectedMood(); // "😊"
        String notes = notesEditText.getText().toString();
        
        // Get symptom values
        List<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom(0, 0, "Cramps", crampsSeekBar.getProgress()));
        symptoms.add(new Symptom(0, 0, "Fatigue", fatigueSeekBar.getProgress()));
        symptoms.add(new Symptom(0, 0, "Mood Swings", moodSwingsSeekBar.getProgress()));
        symptoms.add(new Symptom(0, 0, "Bloating", bloatingSeekBar.getProgress()));
        
        // Calculate phase
        String phase = calculateCurrentPhase(userId);
        
        // Save to database
        long entryId = dbManager.saveCycleEntry(userId, selectedDate, phase, mood, notes, symptoms);
        
        if (entryId > 0) {
            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
```

## 📅 Date Format

All dates in the database use the format: **yyyy-MM-dd** (e.g., "2025-01-20")

Use `DateUtils.getCurrentDate()` to get today's date in the correct format.

## 💡 Tips

1. **Always use DatabaseManager.getInstance()** - It's a singleton, so you get the same instance everywhere
2. **Store userId in SharedPreferences** - After login, save the userId and retrieve it when needed
3. **Use DateUtils for date operations** - Don't manually format dates
4. **Check for null** - Always check if database operations return null before using the data
5. **Handle errors gracefully** - Show user-friendly error messages

## 🔐 Session Management

```java
// Save userId after login
SharedPreferences prefs = getSharedPreferences("MyCyclePrefs", MODE_PRIVATE);
prefs.edit().putLong("userId", userId).apply();

// Get userId when needed
long userId = prefs.getLong("userId", -1);
if (userId == -1) {
    // User not logged in - redirect to SignInActivity
}
```

---

*For more details, see the DatabaseManager and DBHelper classes.*

