package com.careercoach.api.service;

import com.careercoach.api.event.AIServiceCallEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AI 성능 모니터링 서비스
 * AI 서비스 호출 비용 및 성능을 추적하는 시스템입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIPerformanceMonitor {

    // 성능 메트릭 저장소
    private final Map<String, ServiceMetrics> serviceMetrics = new ConcurrentHashMap<>();
    
    // 비용 추적
    private final AtomicLong totalCost = new AtomicLong(0);
    private final AtomicLong totalCalls = new AtomicLong(0);

    /**
     * AI 서비스 호출 이벤트 수신
     * ApplicationEventPublisher를 사용하여 이벤트 발행 로직을 추가합니다.
     */
    @EventListener
    public void handleAIServiceCallEvent(AIServiceCallEvent event) {
        log.info("AI 성능 모니터링 - 서비스: {}, 지속시간: {}ms, 상태: {}", 
                event.getServiceName(), event.getDuration(), event.getStatus());

        try {
            // 메트릭 업데이트
            updateMetrics(event);
            
            // 비용 계산
            calculateCost(event);
            
            // 성능 임계값 체크
            checkPerformanceThresholds(event);
            
            // 모니터링 데이터 저장
            saveMonitoringData(event);

        } catch (Exception e) {
            log.error("AI 성능 모니터링 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 메트릭 업데이트
     */
    private void updateMetrics(AIServiceCallEvent event) {
        String serviceName = event.getServiceName();
        
        serviceMetrics.computeIfAbsent(serviceName, k -> new ServiceMetrics())
                .updateMetrics(event.getDuration(), "SUCCESS".equals(event.getStatus()));
        
        totalCalls.incrementAndGet();
    }

    /**
     * 비용 계산
     */
    private void calculateCost(AIServiceCallEvent event) {
        // 실제 구현에서는 AI 서비스별 비용 정책에 따라 계산
        // 현재는 목업 비용 계산
        long cost = calculateServiceCost(event.getServiceName(), event.getDuration());
        totalCost.addAndGet(cost);
        
        log.debug("AI 서비스 비용 계산 - 서비스: {}, 비용: {}원", event.getServiceName(), cost);
    }

    /**
     * 서비스별 비용 계산 (목업)
     */
    private long calculateServiceCost(String serviceName, long duration) {
        // 기본 비용 + 시간당 비용
        long baseCost = switch (serviceName) {
            case "OpenAI" -> 100; // 100원 기본 비용
            case "Claude" -> 150; // 150원 기본 비용
            default -> 50; // 50원 기본 비용
        };
        
        // 시간당 비용 (1초당 10원)
        long timeCost = (duration / 1000) * 10;
        
        return baseCost + timeCost;
    }

    /**
     * 성능 임계값 체크
     */
    private void checkPerformanceThresholds(AIServiceCallEvent event) {
        String serviceName = event.getServiceName();
        long duration = event.getDuration();
        
        // 응답 시간 임계값 체크
        if (duration > 10000) { // 10초 이상
            log.warn("AI 서비스 응답 시간 초과 - 서비스: {}, 지속시간: {}ms", serviceName, duration);
            // 실제 구현에서는 알림 시스템으로 전송
        }
        
        // 오류율 체크
        ServiceMetrics metrics = serviceMetrics.get(serviceName);
        if (metrics != null && metrics.getErrorRate() > 0.1) { // 10% 이상 오류율
            log.warn("AI 서비스 오류율 높음 - 서비스: {}, 오류율: {:.2f}%", 
                    serviceName, metrics.getErrorRate() * 100);
        }
    }

    /**
     * 모니터링 데이터 저장
     */
    private void saveMonitoringData(AIServiceCallEvent event) {
        // 실제 구현에서는 Prometheus, Datadog 등의 모니터링 툴로 메트릭 전송
        log.info("AI Service Call: {}, Duration: {}ms, Status: {}", 
                event.getServiceName(), event.getDuration(), event.getStatus());
        
        // 예시: Prometheus 메트릭 전송
        // prometheusService.recordHistogram("ai_service_duration", event.getDuration(), 
        //     "service", event.getServiceName());
        // prometheusService.incrementCounter("ai_service_calls_total", 
        //     "service", event.getServiceName(), "status", event.getStatus());
    }

    /**
     * 성능 리포트 생성
     */
    public String generatePerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== AI 서비스 성능 리포트 ===\n");
        report.append(String.format("생성 시간: %s\n", LocalDateTime.now()));
        report.append(String.format("총 호출 수: %d\n", totalCalls.get()));
        report.append(String.format("총 비용: %d원\n", totalCost.get()));
        report.append(String.format("평균 비용: %.2f원\n", 
                totalCalls.get() > 0 ? (double) totalCost.get() / totalCalls.get() : 0));
        
        report.append("\n=== 서비스별 상세 메트릭 ===\n");
        serviceMetrics.forEach((serviceName, metrics) -> {
            report.append(String.format("\n[%s]\n", serviceName));
            report.append(String.format("  총 호출 수: %d\n", metrics.getTotalCalls()));
            report.append(String.format("  성공률: %.2f%%\n", (1 - metrics.getErrorRate()) * 100));
            report.append(String.format("  평균 응답 시간: %.2fms\n", metrics.getAverageResponseTime()));
            report.append(String.format("  최대 응답 시간: %dms\n", metrics.getMaxResponseTime()));
            report.append(String.format("  최소 응답 시간: %dms\n", metrics.getMinResponseTime()));
        });
        
        return report.toString();
    }

    /**
     * 비용 리포트 생성
     */
    public String generateCostReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== AI 서비스 비용 리포트 ===\n");
        report.append(String.format("생성 시간: %s\n", LocalDateTime.now()));
        report.append(String.format("총 비용: %d원\n", totalCost.get()));
        report.append(String.format("총 호출 수: %d\n", totalCalls.get()));
        report.append(String.format("평균 비용: %.2f원\n", 
                totalCalls.get() > 0 ? (double) totalCost.get() / totalCalls.get() : 0));
        
        // 서비스별 비용 분석
        report.append("\n=== 서비스별 비용 분석 ===\n");
        serviceMetrics.forEach((serviceName, metrics) -> {
            long estimatedCost = calculateServiceCost(serviceName, (long) metrics.getAverageResponseTime()) * metrics.getTotalCalls();
            report.append(String.format("%s: 약 %d원 (예상)\n", serviceName, estimatedCost));
        });
        
        return report.toString();
    }

    /**
     * 서비스 메트릭 클래스
     */
    private static class ServiceMetrics {
        private final AtomicLong totalCalls = new AtomicLong(0);
        private final AtomicLong errorCalls = new AtomicLong(0);
        private final AtomicLong totalDuration = new AtomicLong(0);
        private final AtomicLong maxDuration = new AtomicLong(0);
        private final AtomicLong minDuration = new AtomicLong(Long.MAX_VALUE);

        public void updateMetrics(long duration, boolean success) {
            totalCalls.incrementAndGet();
            if (!success) {
                errorCalls.incrementAndGet();
            }
            
            totalDuration.addAndGet(duration);
            
            // 최대/최소 응답 시간 업데이트
            maxDuration.updateAndGet(current -> Math.max(current, duration));
            minDuration.updateAndGet(current -> Math.min(current, duration));
        }

        public long getTotalCalls() { return totalCalls.get(); }
        public double getErrorRate() { 
            long total = totalCalls.get();
            return total > 0 ? (double) errorCalls.get() / total : 0.0;
        }
        public double getAverageResponseTime() { 
            long total = totalCalls.get();
            return total > 0 ? (double) totalDuration.get() / total : 0.0;
        }
        public long getMaxResponseTime() { return maxDuration.get(); }
        public long getMinResponseTime() { 
            long min = minDuration.get();
            return min == Long.MAX_VALUE ? 0 : min;
        }
    }
} 