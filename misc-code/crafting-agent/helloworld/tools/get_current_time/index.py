from datetime import datetime, timezone


def get_current_time():
    now_utc = datetime.now(timezone.utc)
    now_local = now_utc.astimezone()
    tz_name = now_local.tzname()
    return (
        f"Current time (UTC):    {now_utc.strftime('%Y-%m-%dT%H:%M:%S %Z')}\n"
        f"Current time (local):  {now_local.strftime('%Y-%m-%dT%H:%M:%S')} {tz_name}\n"
        f"Day of week:           {now_local.strftime('%A')}\n"
        f"Unix timestamp:        {int(now_utc.timestamp())}"
    )


schema = {
    "type": "function",
    "function": {
        "name": "get_current_time",
        "description": "Get the current date and time in UTC and local timezone. Returns ISO 8601 formatted timestamps, day of week, and Unix timestamp.",
        "parameters": {
            "type": "object",
            "properties": {},
            "required": [],
        },
    },
}

func = get_current_time
