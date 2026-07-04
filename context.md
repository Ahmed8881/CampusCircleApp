# CampusCircleApp — Context for New Agent

## Project Locations
```
D:\Projects\CampusCircle\
├── CampusCircleApp\          # Native Android (Kotlin) mobile app ← YOU ARE HERE
│   ├── app\src\main\
│   │   ├── java\com\example\campuscircleapp\
│   │   │   ├── core\theme\           # ThemeManager, ChartThemeHelper
│   │   │   ├── features\
│   │   │   │   ├── auth\             # Login, Signup, ForgetPassword, EmailVerification
│   │   │   │   ├── home\fragments\   # Student screens (Dashboard, EnrolledCourses, Attendance, etc.)
│   │   │   │   ├── admin\fragments\  # Admin screens
│   │   │   │   ├── admin\viewModels\ # Admin ViewModels
│   │   │   │   ├── admin\models\     # Admin data models
│   │   │   │   ├── teacher\fragments\# Teacher screens
│   │   │   │   ├── onboarding\       # Onboarding flow
│   │   │   │   └── notifications\    # Notification screens
│   │   │   ├── adapters\             # RecyclerView adapters
│   │   │   ├── BaseActivity.kt       # Applies theme before onCreate
│   │   │   ├── AttendenceActivity.kt # Student host activity (extends BaseActivity)
│   │   │   ├── AdminActivity.kt      # Admin host activity (has ThemeManager.applyTheme)
│   │   │   ├── TeacherActivity.kt    # Teacher host activity (has ThemeManager.applyTheme)
│   │   │   └── shared\SplashScreen.kt# Entry point, checks onboarding + auth
│   │   └── res\
│   │       ├── values\               # themes.xml, attrs.xml, styles.xml, colors.xml, theme_*.xml
│   │       ├── values-night\         # Dark mode theme overrides
│   │       ├── layout\               # All screen layouts
│   │       ├── drawable\             # Vectors, backgrounds, gradients
│   │       ├── font\                 # Downloadable font XMLs
│   │       ├── mipmap-*\             # App icons (web logo)
│   │       └── menu\                 # Bottom nav + profile menus
│   └── app\build.gradle.kts
├── SeekhoFrontEnd\           # Angular 18 web app (source of truth for design)
└── GuruPortal\               # (unrelated)
```

## Theme System Summary

### 10 Themes (20 styles)
`Theme.CampusCircleApp.{Name}Light` / `{Name}Dark` for:
seckho, ocean, midnight, forest, lavender, sunset, rose, slate, aubergine, gold

### Key Theme Attributes (defined in `attrs.xml`)
**Surface:** `colorSurfaceElevated`, `colorSurfaceWarm`, `colorSurfaceSidebar`, `colorSurfaceOverlay`
**Text:** `colorTextHeading`, `colorTextBody`, `colorTextMuted`, `colorTextOnPrimary`
**Border:** `colorBorderDefault`, `colorBorderHover`
**Brand:** `colorBrandPrimary`, `colorBrandPrimaryHover`, `colorBrandAccent`, `colorBrandBright`
**Semantic:** `colorSuccess`, `colorWarning`, `colorError`, `colorSurfaceSuccess`, `colorSurfaceError`
**Chart:** `colorChartLine`, `colorChartGrid`, `colorChartFill`, `colorChartText`

### ThemeManager API
```kotlin
ThemeManager.applyTheme(activity)
ThemeManager.getSavedThemeId(context)    // "seckho" etc.
ThemeManager.isDarkMode(context)
ThemeManager.saveTheme(context, id)
ThemeManager.saveDarkMode(context, bool)
ThemeManager.toggleDarkMode(context): Boolean
ThemeManager.toggleDarkModeAndRecreate(activity)
ThemeManager.resolveColorCompat(context, R.attr.colorXxx): Int
ThemeManager.isOnboardingCompleted(context)
ThemeManager.setOnboardingCompleted(context)
```

### ChartThemeHelper API
```kotlin
ChartThemeHelper.lineColor(ctx)     ChartThemeHelper.fillColor(ctx)
ChartThemeHelper.gridColor(ctx)     ChartThemeHelper.textColor(ctx)
ChartThemeHelper.headingColor(ctx)  ChartThemeHelper.bodyColor(ctx)
ChartThemeHelper.mutedColor(ctx)    ChartThemeHelper.surfaceColor(ctx)
ChartThemeHelper.brandPrimary(ctx)  ChartThemeHelper.brandAccent(ctx)
ChartThemeHelper.successColor(ctx)  ChartThemeHelper.warningColor(ctx)
ChartThemeHelper.errorColor(ctx)    ChartThemeHelper.brandBright(ctx)
ChartThemeHelper.attendancePieColors(ctx): Pair<Int,Int>
```

## App Flow
```
App Launch → SplashScreen
  ├── Permission check (POST_NOTIFICATIONS)
  ├── Onboarding? (first time & not logged in) → OnboardingActivity → LoginActivity
  ├── Logged in? → route by role: AttendenceActivity, AdminActivity, TeacherActivity
```

## Web → Mobile Screen Mapping (Updated)

| Web Route | Android File | Status |
|-----------|-------------|--------|
| `/` (Landing) | `OnboardingActivity` | ✅ Done (4 pages, user images) |
| `/login` | `LoginActivity` | ✅ Theme-aware, web logo |
| `/signup` | `SignupActivity` | ✅ Theme-aware, web logo |
| `/forgot-password` | `ForgetPassword` | ✅ Theme-aware, back button fixed |
| `/email-verification` | `EmailVerificationActivity` | ✅ Theme applied |
| `/student/dashboard` | `DashboardFragment` | ✅ Charts theme-aware |
| `/student/dashboard/settings` | `SettingsFragment` | ✅ Theme picker + dark mode + dynamic role |
| `/admin/dashboard/overview` | `AdminDashboardFragment` | ✅ Theme-aware hero, readable pie chart |
| `/admin/dashboard/attendance` | `MarkAttendanceFragment` | ✅ Dark theme student list |
| `/admin/dashboard/announcements` | `AdminAnnouncementsFragment` | ✅ Theme-aware |
| `/admin/dashboard/create-workspace` | `CreateWorkspaceFragment` | ✅ Full columns + CRUD + status toggle |
| `/admin/dashboard/create-semester` | `CreateSemesterFragment` | ✅ Fixed API + Edit/Delete |
| `/admin/dashboard/create-instructors` | `CreateInstructorsFragment` | ✅ Fixed API + Edit/Delete |
| `/admin/dashboard/assignments` | `AdminAssignmentsFragment` | ✅ Full web feature (3 assign types) |
| `/admin/dashboard/timetable` | `AdminTimetableFragment` | ✅ Edit modal + time picker + delete |
| `/admin/dashboard/settings` | `SettingsFragment` (via profile menu) | ✅ Theme/dark mode |
| `/teacher/dashboard` | `TeacherDashboardFragment` | ✅ Charts theme-aware |
| `/teacher/dashboard/courses` | `TeacherCoursesFragment` | ✅ Theme-aware |
| `/teacher/dashboard/students` | `StudentsFragment` | ✅ Theme-aware |
| `/teacher/dashboard/announce-assessment` | `AnnounceAssessmentFragment` | ✅ Theme-aware |

## How to Run / Build
```powershell
cd D:\Projects\CampusCircle\CampusCircleApp
.\gradlew.bat assembleDebug          # Build debug APK
.\gradlew.bat installDebug            # Install on connected device
.\gradlew.bat clean                   # Clean build
```

## Navigation
No Jetpack Navigation. Uses `FragmentManager.beginTransaction().replace()`. Bottom nav via `BottomNavigationView` inside a MaterialCardView. Admin settings accessible from profile picture popup menu (not bottom nav). Student settings in bottom nav.

## Activities that MUST call ThemeManager.applyTheme(this)
- `BaseActivity` (parent) ✅
- `SplashScreen` ✅
- `OnboardingActivity` ✅
- `AdminActivity` ✅ (added fix — was missing)
- `TeacherActivity` ✅ (added fix — was missing)
- `SignupActivity` ✅ (added fix — was missing)
- `ForgetPassword` ✅ (added fix — was missing)
- `EmailVerificationActivity` ✅ (added fix — was missing)

## API Notes
- Backend: `https://campus-circle.hserver321.dpdns.org/api/`
- Instructors: `GET User/GetAllInstructors` (with 404→empty list fallback)
- Semesters: `GET Semester/GetAllSemesters` (with 404→empty list fallback)
- Students: `GET User/GetAllStudents` (with 404→empty list fallback)
- Workspace: `Space/GetSpaces`, `Space/CreateSpace` (multipart), `Space/UpdateSpace`, `Space/DeleteSpace`, `Space/ActiveAndInActiveSpaces`
- Semester: `Semester/CreateSemester`, `Semester/UpdateSemester`, `Semester/DeleteSemester`
- Instructor: `User/AddInstructor`, `User/DeleteInstructor` (no update endpoint per web pattern)
- Assignments: `Space/AssignSeamester`, `User/AssignSpacesToInstructor`, `User/AssignCR_Of_Space`
- Timetable: `Timetable/GetTimetable?spaceId=1`, `Timetable/CreateTimetable`, `Timetable/UpdateTimetable`, `Timetable/DeleteTimetable?id=`

## Known Remaining Items
1. **Teacher Attendance** — `AttendanceManagementFragment` shared between admin/teacher, verify dark theme works
2. **Notification detail screen** — May need theme audit
3. **Admin Manage screen** — could add direct Settings option there alongside existing management cards
4. **Gradient drawables** — `bg_gradient_primary.xml` etc. still have hardcoded colors (gradients don't support `?attr/` in all SDK versions)
5. **Charts** — should recreate on theme change (currently need activity recreate)
