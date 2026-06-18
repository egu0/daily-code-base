---
name: myrta-test-availability
description: Check NSW myRTA driving test booking availability through Manage Booking. Use when the user asks Codex to log in to https://www.myrta.com/ with a booking number and family name, inspect available driving test times, search a requested test location by suburb name plus postcode, and report availability without submitting or confirming any booking change.
---

# myRTA Test Availability

## Overview

Use this skill to safely inspect myRTA driving test availability for an existing booking. The workflow is read-only until the user explicitly asks to select and confirm a new appointment; by default, never submit a final change.

## Required Inputs

- Booking number.
- Family name.
- Target location as suburb name plus postcode, for example `Silverwater 2128`.

If the user provides a suburb and an invalid-looking postcode, do not silently correct it unless the page itself clearly resolves the intended location. Report both the requested text and the site-resolved location.

## Browser Workflow

1. Use the browser automation surface available in the current environment. If a playwright-cli skill is available, then use it.
2. Open `https://www.myrta.com/`.
3. On the home page, choose `Manage booking` under the `Book a test` area.
4. In `Have a booking number?`, enter the booking number and family name, then continue.
5. Choose `Change location` to enter the location/date availability flow. This is not a final booking change.
6. On the choose-location screen, read the earliest allowed date from the page text, usually `The date preferred cannot be before DD/MM/YYYY`.
7. Set `Date preferred` to that earliest allowed date.
8. Resolve the requested location:
   - Select the `Search by suburb` radio button first to reveal the suburb combobox.
   - Do **not** use `Search by location` — it only lists Service NSW centres by name and cannot be filtered by suburb+postcode, so it will not find the intended test location reliably.
   - The suburb field is a combobox driven by autocomplete. To trigger the dropdown, first `click` the textbox to focus it, then use `type` to input the suburb name character by character (e.g. `playwright-cli type "Silverwater"`). Do **not** use `fill` — it replaces all text at once and will not fire the `keydown`/`input` events the autocomplete depends on.
   - Wait for autocomplete suggestions to appear, then choose the result that matches the requested suburb and postcode (e.g. `SILVERWATER, 2128`).
   - Click `Find a location`.
   - From the returned location list, select the exact radio option (e.g. `SILVERWATER 2128`), before continuing.
9. Click `Next` to reach `Choose an available time`.
10. Inspect weekly availability pages until the 14-calendar-day search window is covered.
    - If the page shows `There are no timeslots available for this week.`, treat that entire week as having no availability regardless of any time links rendered in the time grid.
11. Do not click any timeslot unless the user explicitly asks to reserve that specific slot.
12. At the end of the lookup, click the page's `Quit` button. When the modal appears with `When you exit to the homepage, your session will end and any information you have provided will be lost.`, choose `Exit to homepage`, not `Continue with transaction`. Confirm that the browser returns to the myRTA home page before finalizing/cleaning up browser tabs according to the browser tool's rules.

## Date Window Rule

Do not hard-code dates.

1. Determine `earliest_date` from the myRTA page prompt.
2. Define the search window as `earliest_date` through `earliest_date + 14 calendar days`, inclusive unless the user asks for a different rule.
3. Start with the weekly page containing `earliest_date`.
4. Use `Next week` until every date in the 14-day window has been inspected.
5. Ignore dates before `earliest_date` and dates after the 14-day window, even if they appear in a weekly grid.

## Availability Interpretation

- Treat an explicit message such as `There are no timeslots available for this week.` as authoritative for that displayed week.
- If the page renders a generic time grid while also showing the no-timeslots message, report no availability for that week. Do not treat template links like `8:00 am`, `8:30 am`, etc. as valid slots unless selecting them reveals a real, enabled slot state and no no-timeslots message contradicts it.
- If times are truly available, report them grouped by date. Include the resolved location.
- If no slots are available, say which date range and weekly pages were checked.

## Safety Rules

- Never submit or confirm a booking change unless the user explicitly asks for that exact final action.
- Never cancel a booking unless the user explicitly asks and confirms at action time.
- Do not send booking confirmations by email unless requested.
- If the site shows a CAPTCHA, permission prompt, payment step, or final confirmation step, pause and ask the user.
- If the standard `Quit` modal is unavailable, avoid destructive navigation and report that normal exit could not be completed.

## Final Response

Answer in the user's language. Keep the final response concise. Include only:

- Query location.
- Query date window.
- Result, including only available slots when any exist.
- Checked weekly pages and each weekly page's availability summary.

Use this shape:

```markdown
查询地点：Silverwater 2128
查询窗口：20/06/2026 - 04/07/2026（含）

结果：有考位

- Friday, 03 July 2026 8:15 AM

检查过的周页：

- Week starting 15/06/2026: 无 timeslots
- Week starting 22/06/2026: 无 timeslots
- Week starting 29/06/2026: 03/07/2026 8:15 AM 可用
```

If there are no available slots, use `结果：无考位` and omit slot bullets under the result.
