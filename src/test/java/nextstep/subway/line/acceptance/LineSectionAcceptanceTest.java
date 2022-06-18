package nextstep.subway.line.acceptance;

import static nextstep.subway.line.acceptance.LineAcceptanceSteps.*;
import static nextstep.subway.line.acceptance.LineSectionAcceptanceSteps.*;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 광교역;

    /**
     * Feature: 지하철 구간 관련 기능
     *
     *   Background
     *     Given 지하철역 등록되어 있음
     *     And 지하철 노선 등록되어 있음
     *     And 지하철 노선에 지하철역 등록되어 있음
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    /**
     *   Scenario: 지하철 구간을 관리 - 성공
     *     When 지하철 구간 등록 요청
     *     Then 지하철 구간 등록됨
     *     When 지하철 노선에 등록된 역 목록 조회 요청
     *     Then 등록한 지하철 구간이 반영된 역 목록이 조회됨
     *     When 지하철 구간 삭제 요청
     *     Then 지하철 구간 삭제됨
     *     When 지하철 노선에 등록된 역 목록 조회 요청
     *     Then 삭제한 지하철 구간이 반영된 역 목록이 조회됨
     */
    @Test
    @DisplayName("성공한 통합 인수 테스트")
    void successIntegrationTest() {
        // when
        ExtractableResponse<Response> lineSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 3);
        // then
        지하철_노선에_지하철역_등록됨(lineSectionResponse);

        // when
        ExtractableResponse<Response> sectionsResponse = 지하철_노선_조회_요청(신분당선);
        // then
        지하철_노선에_지하철역_순서_정렬됨(sectionsResponse, Arrays.asList(강남역, 양재역, 광교역));

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);
        // then
        지하철_노선에_지하철역_제외됨(removeResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        // then
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 광교역));
    }

    /**
     *   Scenario: 지하철 구간을 관리 - 실패
     *     When 지하철 구간 등록 요청
     *     Then 노선에 등록되지 않은 역을 기준으로 등록 실패됨
     *     When 지하철 구간 삭제 요청
     *     Then 노선에 등록된 지하철역이 두개일 때 삭제 실패됨
     */
    @Test
    @DisplayName("실패한 통합 인수 테스트")
    void failIntegrationTest() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 3);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 강남역);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }
}
