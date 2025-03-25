package io.hhplus.tdd.point.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PointController.class)
public class PointControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PointService pointService;

    @DisplayName("사용자 조회 요청 성공 시 UserPoint를 반환한다.")
    @Test
    void get_point_id_request_return_user_point_object() throws Exception {

        long id = 1L;
        UserPoint result = UserPoint.empty(id);
        when(pointService.selectById(id)).thenReturn(result);

        mvc.perform(get("/point/1"))
                //mvc.perform()의 결과를 검증
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(0L))
                .andDo(print());
    }

    @DisplayName("포인트 이력 조회 요청 시 조회된 이력 목록을 반환한다.")
    @Test
    void get_point_history_request_return_point_history_list() throws Exception {
        long cursor = 1L;
        PointHistory pointHistory1 = new PointHistory(cursor++, 1L, 3000L, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = new PointHistory(cursor++, 1L, 4000L, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory3 = new PointHistory(cursor++, 1L, 5000L, TransactionType.CHARGE, System.currentTimeMillis());
        List<PointHistory> pointHistories = List.of(pointHistory1, pointHistory2, pointHistory3);

        when(pointService.selectPointHistoryAllByUserId(1L)).thenReturn(pointHistories);

        MvcResult result = mvc.perform(get("/point/1/histories"))
                //mvc.perform()의 결과를 검증
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<PointHistory> resultList = objectMapper.readValue(content, new TypeReference<>(){});

        assertEquals(pointHistories.size(), resultList.size());
    }

    @DisplayName("사용자 포인트 충전 요청 성공 시 충전 금액이 추가된 UserPoint 객체를 응답한다.")
    @Test
    void charge_point_request_return_current_user_point_object() throws Exception {
        long id = 1L; long chargeAmount = 5_000L;
        UserPoint defaultUserPoint = UserPoint.empty(id);
        when(pointService.charge(id, chargeAmount))
                .thenReturn(new UserPoint(defaultUserPoint.id(), defaultUserPoint.point()+ chargeAmount, System.currentTimeMillis()));

        String requestBody = objectMapper.writeValueAsString(chargeAmount);

        MvcResult mvcResult = mvc.perform(
                        patch("/point/"+id+"/charge")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        UserPoint result = objectMapper.readValue(content, UserPoint.class);

        assertEquals(defaultUserPoint.id(), result.id());
        assertEquals((defaultUserPoint.point() + chargeAmount), result.point());
    }

    @DisplayName("사용자 포인트 사용 요청 성공 시 사용 금액이 차감된 UserPoint 객체를 응답한다.")
    @Test
    void use_point_request_return_current_user_point_object() throws Exception {
        long id = 1L; long useAmount = 5_000L;
        UserPoint defaultUserPoint = new UserPoint(1L, 50_000L, System.currentTimeMillis());
        when(pointService.use(id, useAmount))
                .thenReturn(new UserPoint(defaultUserPoint.id(), defaultUserPoint.point() - useAmount, System.currentTimeMillis()));

        String requestBody = objectMapper.writeValueAsString(useAmount);

        MvcResult mvcResult = mvc.perform(
                        patch("/point/"+id+"/use")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        UserPoint result = objectMapper.readValue(content, UserPoint.class);

        assertEquals(defaultUserPoint.id(), result.id());
        assertEquals((defaultUserPoint.point() - useAmount), result.point());
    }


}
