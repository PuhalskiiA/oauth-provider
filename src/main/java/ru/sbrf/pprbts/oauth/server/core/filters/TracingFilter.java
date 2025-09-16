package ru.sbrf.pprbts.oauth.server.core.filters;

import brave.Span;
import brave.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sbrf.pprbts.oauth.server.core.utilities.Constants;
import ru.sbrf.pprbts.oauth.server.core.utilities.MdcUtils;


/**
 * Фильтр настройки traceId.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TracingFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    @NonNull
    private static String getNewSpanName() {
        return "ServiceRequest-%s".formatted(RandomStringUtils.randomAlphanumeric(32));
    }

    @Override
    @SneakyThrows
    public void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        log.trace("TracingFilter: doFilterInternal");
        Span newSpan = tracer.newTrace()
                .name(getNewSpanName())
                .start();

        try (Tracer.SpanInScope ignored = tracer.withSpanInScope(newSpan)) {
            String currentTraceId = newSpan.context().traceIdString();
            log.trace("TracingFilter: traceId: {}", currentTraceId);

            request.setAttribute(Constants.Request.Context.TRACE_ID, currentTraceId);
            response.addHeader(Constants.Request.TRACE_ID_HEADER, currentTraceId);
            MdcUtils.setValue(Constants.Request.Context.TRACE_ID, currentTraceId);

            filterChain.doFilter(request, response);
        } catch (Exception | Error e) {
            newSpan.error(e);
            throw e;
        } finally {
            newSpan.finish();
            MdcUtils.setValue(Constants.Request.Context.TRACE_ID, null);
        }
    }
}
