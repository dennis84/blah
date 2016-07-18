fixtures() {
  local ua_chromium="Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 \
                     (KHTML, like Gecko) Ubuntu Chromium/45.0.2454.85 \
                     Chrome/45.0.2454.85 Safari/537.36"

  # -------------------------------------------------------
  # User: dennis
  # -------------------------------------------------------
  create_event "pageviews" "$(printf '{
    "item": "landingpage",
    "title": "Viewed landing page",
    "user": "dennis",
    "userAgent": "%s",
    "referrer": "google.com"
  }' "$ua_chromium")"

  create_event "pageviews" "$(printf '{
    "item": "signup",
    "title": "Viewed signup page",
    "user": "dennis",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "registrations" "$(printf '{
    "title": "Created a new account",
    "user": "dennis",
    "email": "d.dietrich84@gmail.com",
    "firstname": "Dennis",
    "lastname": "Dietrich",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "pageviews" "$(printf '{
    "item": "dashboard",
    "title": "Viewed dashboard page",
    "user": "dennis",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "products" "$(printf '{
    "item": "the-fellowship-of-the-ring",
    "title": "Bought The Fellowship of the Ring",
    "user": "dennis"
  }' "$ua_chromium")"

  create_event "products" "$(printf '{
    "item": "the-two-towers",
    "title": "Bought The Two Towers",
    "user": "dennis"
  }' "$ua_chromium")"

  create_event "products" "$(printf '{
    "item": "the-hobbit-an-unexpected-journey",
    "title": "Bought The Hobbit: An Unexpected Journey",
    "user": "dennis"
  }' "$ua_chromium")"

  # -------------------------------------------------------
  # User: john
  # -------------------------------------------------------
  create_event "pageviews" "$(printf '{
    "item": "landingpage",
    "title": "Viewed landing page",
    "user": "john",
    "userAgent": "%s",
    "referrer": "google.com"
  }' "$ua_chromium")"

  create_event "pageviews" "$(printf '{
    "item": "signup",
    "title": "Viewed signup page",
    "user": "john",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "pageviews" "$(printf '{
    "item": "pricing",
    "title": "Viewed pricing page",
    "user": "john",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "pageviews" "$(printf '{
    "item": "signup",
    "title": "Viewed signup page",
    "user": "john",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "registrations" "$(printf '{
    "title": "Created a new account",
    "user": "john",
    "email": "john@example.com",
    "firstname": "John",
    "lastname": "Doe",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "pageviews" "$(printf '{
    "item": "dashboard",
    "title": "Viewed dashboard page",
    "user": "john",
    "userAgent": "%s"
  }' "$ua_chromium")"

  create_event "products" "$(printf '{
    "item": "the-two-towers",
    "title": "Bought The Two Towers",
    "user": "john"
  }' "$ua_chromium")"

  create_event "products" "$(printf '{
    "item": "the-return-of-the-king",
    "title": "Bought The Return of the King",
    "user": "john"
  }' "$ua_chromium")"
}
