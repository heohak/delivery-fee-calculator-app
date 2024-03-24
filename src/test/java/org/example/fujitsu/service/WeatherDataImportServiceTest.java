package org.example.fujitsu.service;

import org.example.fujitsu.entity.WeatherData;
import org.example.fujitsu.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherDataImportServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @InjectMocks
    private WeatherDataImportService service;

    private String mockXml;

    @BeforeEach
    void setUp() {
        mockXml = """
            <observations timestamp="1711143313">
                <station>
                    <name>Tallinn-Harku</name>
                    <wmocode>26038</wmocode>
                    <longitude>24.602891666624284</longitude>
                    <latitude>59.398122222355134</latitude>
                    <phenomenon>Light rain</phenomenon>
                    <visibility>35.0</visibility>
                    <precipitations>1</precipitations>
                    <airpressure>993.2</airpressure>
                    <relativehumidity>97</relativehumidity>
                    <airtemperature>3.7</airtemperature>
                    <winddirection>220</winddirection>
                    <windspeed>3.2</windspeed>
                    <windspeedmax>6.3</windspeedmax>
                </station>
            </observations>
            """;
    }

    @Test
    void whenValidXml_thenDataIsSavedCorrectly() {
        lenient().when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockXml);

        service.importWeatherData();
        verify(weatherDataRepository, atLeastOnce()).save(any(WeatherData.class));
    }



    @Test
    void whenPhenomenonTagIsEmpty_thenDataIsStillSaved() {
        String xmlWithEmptyPhenomenon = """
        <observations timestamp="1711143313">
            <station>
                <name>Tallinn-Harku</name>
                <wmocode>26038</wmocode>
                <airtemperature>3.7</airtemperature>
                <windspeed>3.2</windspeed>
                <phenomenon></phenomenon>
            </station>
        </observations>
        """;
        lenient().when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlWithEmptyPhenomenon);

        service.importWeatherData();

        verify(weatherDataRepository, atLeastOnce()).save(any(WeatherData.class));
    }




}
