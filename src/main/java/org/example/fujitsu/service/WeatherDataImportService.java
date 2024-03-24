package org.example.fujitsu.service;

import lombok.AllArgsConstructor;
import org.example.fujitsu.entity.WeatherData;
import org.example.fujitsu.repository.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
@AllArgsConstructor
@Service
public class WeatherDataImportService {
    private static final Logger log = LoggerFactory.getLogger(WeatherDataImportService.class);
    private static final String WEATHER_DATA_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private WeatherDataRepository weatherDataRepository;

    /**
     * Periodically imports weather data from a predefined URL, parses it,
     * and saves the relevant data to the database. The method is scheduled to run
     * once every hour, 15 minutes after a full hour. It logs warnings for no data and errors for unexpected issues during the import.
     */
    @Scheduled(cron = "0 * * * * *")
    public void importWeatherData() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String weatherDataXml = restTemplate.getForObject(WEATHER_DATA_URL, String.class);
            if (weatherDataXml != null) {
                parseAndSaveWeatherData(weatherDataXml);
            } else {
                log.warn("No data returned from the weather data URL.");
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred during the weather data import: {}", e.getMessage(), e);
        }
    }

    private void parseAndSaveWeatherData(String xmlData) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
            doc.getDocumentElement().normalize();

            String observationsTimestamp = doc.getDocumentElement().getAttribute("timestamp");
            long epochSecond = Long.parseLong(observationsTimestamp);
            LocalDateTime observationDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault());

            NodeList nList = doc.getElementsByTagName("station");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    processStationElement(eElement, observationDateTime);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing weather data XML: {}", e.getMessage(), e);
        }
    }

    private void processStationElement(Element eElement, LocalDateTime observationDateTime) {
        String stationName = eElement.getElementsByTagName("name").item(0).getTextContent();
        if (stationName.equals("Tallinn-Harku") || stationName.equals("Tartu-Tõravere") || stationName.equals("Pärnu")) {
            WeatherData weatherData = new WeatherData();
            weatherData.setStationName(stationName);
            weatherData.setWmoCode(eElement.getElementsByTagName("wmocode").item(0).getTextContent());
            weatherData.setAirTemperature(Double.parseDouble(eElement.getElementsByTagName("airtemperature").item(0).getTextContent()));
            weatherData.setWindSpeed(Double.parseDouble(eElement.getElementsByTagName("windspeed").item(0).getTextContent()));
            weatherData.setWeatherPhenomenon(eElement.getElementsByTagName("phenomenon").item(0).getTextContent());
            weatherData.setObservationTimestamp(observationDateTime);
            weatherDataRepository.save(weatherData);
        }
    }
}
