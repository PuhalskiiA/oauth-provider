package ru.sbrf.pprbts.oauth.server.core.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        public static final String TRACE_ID_HEADER = "X-TraceId";

        public static final String ACTUATOR_HEALTH_CHECK_URL = "/actuator/health/";

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Context {

            public static final String TRACE_ID = "traceId";
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OAuth {

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class RequestParams {

            public static final String CODE = "code";

            public static final String RESPONSE_TYPE = "response_type";

            public static final String CLIENT_ID = "client_id";

            public static final String REDIRECT_URI = "redirect_uri";

            public static final String STATE = "state";

            public static final String SCOPE = "scope";

            public static final String GRANT_TYPE = "grant_type";

            public static final String REFRESH_TOKEN = "refresh_token";
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class GrantType {

            public static final String AUTHORIZATION_CODE = "authorization_code";

            public static final String REFRESH_TOKEN = "refresh_token";
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Token {

            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class Claim {

                public static final String EMAIL = "email";
            }
        }
    }
}
