package kr.hhplus.be.server.application.external.service;

import kr.hhplus.be.server.application.external.dto.ExternalRequestDto;
import org.springframework.stereotype.Service;

@Service
public class ExternalDataPlatformService {

    public boolean sendData(ExternalRequestDto requestDto) {
        // 실제 외부 서비스 전송 로직 대신 무조건 true 반환
        System.out.println("External data sent:");
        System.out.println("User ID: " + requestDto.getUserId());
        System.out.println("Order ID: " + requestDto.getOrderId());
        System.out.println("Amount: " + requestDto.getAmount());

        // 외부 통신 성공 여부 (임의로 true 반환)
        return true;
    }
}