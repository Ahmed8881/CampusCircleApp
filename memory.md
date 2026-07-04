# CampusCircleApp — Memory & Handoff Document

## Project Overview
Native Android (Kotlin) app for Campus Circle, synced with `SeekhoFrontEnd` (Angular 18 web app). Both share the same backend API at `https://campus-circle.hserver321.dpdns.org/api/`.

## What Was Changed

### Phase 1 — Theme & Typography Foundation
- 10 theme files (`theme_*.xml`) with Light + Dark styles for Seckho, Ocean, Midnight, Forest, Lavender, Sunset, Rose, Slate, Aubergine, Gold
- `attrs.xml` with custom theme attributes (surface, text, border, brand, semantic, chart colors)
- `styles.xml` — all text appearances use `@font/inter_*`, widgets use `?attr/` color refs
- 11 downloadable font XML files (Inter, Instrument Serif, Poppins weights)
- `ThemeManager.kt` — singleton for theme/dark mode persistence + `applyTheme()`
- `BaseActivity.kt` — calls `ThemeManager.applyTheme(this)` before `super.onCreate()`
- Font certs removed after `bad base-64` crash — fonts work without explicit certs

### Phase 2 — Shared UI & Charts
- `ChartThemeHelper.kt` — static helper providing theme-aware MPAndroidChart colors
- `DashboardFragment`, `AdminDashboardFragment`, `TeacherDashboardFragment` updated to use `ChartThemeHelper`

### Phase 3 — Onboarding & Auth
- 4-page onboarding carousel (ViewPager2 + dots + Next/Prev/Start)
- Web logo (`logo_web.png`) copied from `SeekhoFrontEnd/public/logo.png`
- User's images used for attendance (`onboarding_attendance.png`) and analytics (`onboarding_growth.png`) pages
- Login + Signup layouts rewritten with `?attr/` + `@font/inter_*`
- SplashScreen routes to onboarding if first time
- `AuthGuard`-like pattern: `ThemeManager.applyTheme(this)` added to `AdminActivity`, `TeacherActivity`, `SignupActivity`, `ForgetPassword`, `EmailVerificationActivity` (all were missing it)

### Phase 4 — Admin Screens (12 layout files updated for theme)
- All admin layouts use `?attr/` color references and `@font/inter_*`

### Phase 5 — Student Screens (13 layouts + 3 Kotlin files)
- All student layouts use `?attr/` + `@font/inter_*`. EventsFragment, AttendanceHistoryAdapter, EnrolledCourseAdapter use `ChartThemeHelper`/`ThemeManager`

### Phase 6 — Teacher Screens (7 layouts updated)

### Phase 7 — Admin Workspace + Semester + Instructors (Full CRUD)
- **WorkspaceScreen**: Full columns (Name, Members, Status toggle, Edit/Delete). New `WorkspaceAdapter`. API: UpdateSpace, DeleteSpace, ActiveAndInActiveSpaces.
- **SemesterScreen**: Fixed API to `GetAllSemesters`, date format to ISO. Edit/Delete dialogs.
- **InstructorsScreen**: Fixed API to `GetAllInstructors`. Edit/Delete. Data model corrections (`userName`, `fullName`).
- New API endpoints added to `ApiService.kt` and `HomeService.kt` for all the above.

### Phase 8 — Admin Assignments (Full web feature)
- Three assign actions: Assign Semester (to space), Assign Instructor (to space), Assign CR
- Search bar filtering instructors/students
- Instructors list + "Assign Space" button, Students list + "Make CR" button
- Spaces overview + "Assign Semester" button
- Three dialogs with dropdowns

### Phase 9 — Admin Timetable (Edit/Delete modals)
- Entry click opens edit modal with day spinner, time picker, room
- Delete button with confirmation
- Add mode via "Add Class" button

### Phase 10 — Admin Settings (Bottom nav + Profile menu)
- Removed Settings from admin bottom nav (reverted to 4 items: Dashboard, Attendance, Announce, Manage)
- Added Settings to profile picture popup menu (matching teacher's existing pattern)
- Added night-mode `bottom_nav_item_color.xml` using `?attr/colorPrimary` + `?attr/colorTextMuted`

### Phase 11 — Dark Theme Fixes (Batch)
- `bottom_nav_item_color.xml` uses `?attr/colorPrimary` (selected) + `?attr/colorTextMuted` (unselected)
- `item_mark_attendance_student.xml` — all hardcoded `@color/` → `?attr/`
- `item_workspace.xml`, `item_semester.xml`, `item_instructor.xml` — hardcoded colors → `?attr/`
- `spinner_background.xml`, `bg_icon_circle.xml`, `bg_pill_primary.xml` — `?attr/` refs
- Admin dashboard hero card bg changed from gradient drawable → `?attr/colorPrimary`

### Phase 12 — Admin List Cleanup
- Removed "Updated At" + "Updated By" from workspace, semester, instructor list items
- Switched instructors/semesters APIs to fallback endpoints with 404-as-empty-list handling
- StudentResponse/InstructorResponse `email` and `username` made nullable to prevent "null" display
- Assign CR dropdown now shows clean format (no `(@)` when username empty)
- Assign CR confirmation dialog added before performing action
- Assign CR `username` null safety fix (was crashing with NPE)

### Phase 13 — Chart Readability + ForgotPassword Fix
- Pie chart value labels moved outside slice with leader lines, use `Color.WHITE` for contrast
- `setCenterTextColor` uses theme-aware `headingColor`
- `ForgetPassword` layout rewritten with `?attr/` + `@font/inter_*`
- Added `backToLogin` click listener (was missing, caused frozen navigation)

### Phase 14 — Timetable Day Edit + Time Picker
- Edit mode now shows day Spinner (was read-only text)
- Time fields replaced with `TimePickerDialog` clock picker
- Start/end time made clickable (keyboard suppressed)

### Phase 15 — Dynamic Role + App Icon
- Settings profile card now shows dynamic role (Admin/Teacher/Student) from `SessionManager`
- Login, Signup, Splash screen logos updated to web logo
- App launcher icon updated to web logo (adaptive icon + mipmap PNGs)

## Build Status
`.\gradlew.bat assembleDebug` — **SUCCESS**

## Key Architecture Decisions
- **Theme switching:** 20 theme styles (10 themes × 2 modes) in XML. `ThemeManager.applyTheme()` called before `super.onCreate()`
- **Fonts:** Downloadable Fonts via Google Play Services (no APK bloat)
- **Charts:** `ChartThemeHelper` reads resolved theme colors at runtime
- **Onboarding:** `SharedPreferences` flag, checked in `SplashScreen`
- **Remaining:** Admin/Teacher activities still need profile Settings in bottom nav (currently in popup menu only)
