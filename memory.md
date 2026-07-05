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

### Phase 16 — Bundled Fonts (July 2026)
- Switched from downloadable Google Fonts (via Play Services) to **bundled offline TTFs**
- Downloaded Inter (Regular, Medium, SemiBold, Bold, Black) and Instrument Serif (Regular, Italic) TTFs into `res/font/`
- Removed all downloadable font XML wrappers (7 Inter + Instrument Serif XMLs)
- Removed unused Poppins font XMLs (4 files)
- This fixes the `Resources$NotFoundException` crash in `EventsFragment.renderCalendar()` (font resource not available offline on some devices)
- All `@font/inter_*` and `@font/instrument_serif_*` references remain unchanged

### Phase 17 — Theme Fixes (July 2026)
- `bg_badge_primary.xml`: hardcoded `@color/color_primary_container` → `?attr/colorPrimaryContainer`
- `fragment_enrolled_courses.xml`: badge "0 active" text color → `?attr/colorOnPrimaryContainer`
- `fragment_timetable.xml`: today card background → `?attr/colorPrimary` (was hardcoded gradient)

### Phase 18 — Profile Picture Upload in Settings (July 2026)
- Added `User/UploadProfilePicture` multipart endpoint to `ApiService.kt` + `HomeService.kt`
- Added `uploadProfilePicture()` + `uploadState` to `SettingsViewModel.kt`
- Added image picker (`ActivityResultContracts.GetContent`) in `SettingsFragment.kt`
- Added URI-to-`MultipartBody.Part` helper in `SettingsFragment.kt`
- Added "Change Profile Picture" clickable card in `fragment_settings.xml`
- Removed `app:tint="?attr/colorPrimary"` from header `ShapeableImageView` in all 3 host activity layouts (so real photos don't get colorized)

### Phase 19 — Logout in Settings (July 2026)
- Created `SettingsLogoutListener` interface in `SettingsFragment.kt`
- Added red "Logout" card with error icon in `fragment_settings.xml`
- Implemented `SettingsLogoutListener` in `AdminActivity`, `AttendenceActivity`, `TeacherActivity`
- Settings Logout calls `SignalRManager.stop()` + `SessionManager.clearToken()` + navigates to login

### Phase 20 — Announcements Rewire (July 2026)
- **Student**: Removed announcements from profile avatar popup menu; notification bell now opens `AnnouncementsFragment`
- **Admin**: Removed notification bell icon from header layout entirely

### Phase 21 — Performance Code Efficiency (July 2026)
- Splash screen delay reduced from 2500ms → 800ms
- HTTP logging interceptor set to `BASIC` in debug, `NONE` in release (via `BuildConfig.DEBUG`)
- Commented out `androidx.benchmark:benchmark-common` from production dependencies (test-only library)
- Enabled `buildConfig = true` in `build.gradle.kts`

## Build Status
`.\gradlew.bat assembleDebug` — **SUCCESS**

## Key Architecture Decisions
- **Theme switching:** 20 theme styles (10 themes × 2 modes) in XML. `ThemeManager.applyTheme()` called before `super.onCreate()`
- **Fonts:** Bundled TTF files in `res/font/` (Inter + Instrument Serif). No downloadable fonts or XML wrappers.
- **Charts:** `ChartThemeHelper` reads resolved theme colors at runtime
- **Onboarding:** `SharedPreferences` flag, checked in `SplashScreen`
- **Profile picture:** Uploaded via `POST User/UploadProfilePicture` (multipart), picked via `ActivityResultContracts.GetContent`
- **Logout in Settings:** Uses callback interface pattern (`SettingsLogoutListener`) so host Activity handles SignalR stop + navigation
- **Remaining:** Profile picture URL needs caching in `SessionManager` for offline resume (currently re-fetched via `getUserData` on every header load)
