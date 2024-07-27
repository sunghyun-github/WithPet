package com.animal.mypet.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApiService {

    private final RestTemplate restTemplate;
    private final HttpClient httpClient;
    private final ApiRepository apiRepository;

    @Autowired
    public ApiService(RestTemplate restTemplate, ApiRepository apiRepository) {
        this.restTemplate = restTemplate;
        this.apiRepository = apiRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    // API URLs
    private final String ggApiUrl = "https://openapi.gg.go.kr/AbdmAnimalProtect?KEY=2489db646b404d58bd848a19a2208e80&pIndex=1&type=json";
    private final String seoulApiUrl = "http://openapi.seoul.go.kr:8088/767a7a5069616c73383572714b6345/json/TbAdpWaitAnimalView/1/1000/";
    private final String seoulImageApiUrl = "http://openapi.seoul.go.kr:8088/4a496f6b69616c733638706444524d/json/TbAdpWaitAnimalPhotoView/1/1000/";

    private final String defaultImageUrl = "https://example.com/default-image.png"; // 기본 이미지 URL 설정

    @Transactional
    public void fetchDataAndSave() {
        fetchAndSaveDataFromGyeonggi();
        fetchAndSaveDataFromSeoul();
    }

    private void fetchAndSaveDataFromGyeonggi() {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(ggApiUrl)).GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("API Response Code: " + response.statusCode());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode itemsNode = rootNode.path("AbdmAnimalProtect").get(1).path("row");

                for (JsonNode itemNode : itemsNode) {
                    ApiEntity animal = convertJsonToAnimal(itemNode);
                    log.debug("Converted Animal: " + animal);
                    try {
                        apiRepository.save(animal);
                        log.info("Saved animal: " + animal);
                    } catch (Exception e) {
                        log.error("Error saving animal: " + e.getMessage(), e);
                    }
                }
            } else {
                log.error("Failed to fetch data from API, status code: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error("An error occurred while fetching data from API: " + e.getMessage(), e);
        }
    }

    private void fetchAndSaveDataFromSeoul() {
        try {
            log.info("Fetching data from Seoul API...");
            String seoulApiResponse = restTemplate.getForObject(seoulApiUrl, String.class);
            String seoulImageApiResponse = restTemplate.getForObject(seoulImageApiUrl, String.class);
            if (seoulApiResponse != null && seoulImageApiResponse != null) {
                saveAnimalsAndPhotosFromSeoulJson(seoulApiResponse, seoulImageApiResponse);
            } else {
                log.warn("No response from Seoul API");
            }
        } catch (Exception e) {
            log.error("Error fetching data from Seoul API: " + e.getMessage(), e);
        }
    }

    private void saveAnimalsAndPhotosFromSeoulJson(String seoulApiResponse, String seoulImageApiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode animalRootNode = objectMapper.readTree(seoulApiResponse).path("TbAdpWaitAnimalView").path("row");
            JsonNode photoRootNode = objectMapper.readTree(seoulImageApiResponse).path("TbAdpWaitAnimalPhotoView").path("row");

            List<ApiEntity> animals = new ArrayList<>();

            for (JsonNode animalNode : animalRootNode) {
                String animalNo = animalNode.path("ANIMAL_NO").asText();
                String imageUrl = "";

                for (JsonNode photoNode : photoRootNode) {
                    if (photoNode.path("ANIMAL_NO").asText().equals(animalNo) && photoNode.path("PHOTO_NO").asInt() == 1) {
                        imageUrl = photoNode.path("PHOTO_URL").asText();
                        break;
                    }
                }

                ApiEntity animal = new ApiEntity();
                animal.setAnimalNo(animalNode.path("ANIMAL_NO").asText());
                animal.setNm(animalNode.path("NM").asText());
                animal.setIntrcnCn(animalNode.path("INTRCN_CN").asText());
                animal.setPhotoUrl(imageUrl);
                animal.setSexdstn(animalNode.path("SEXDSTN").asText()); // 성별
                animal.setEntrncDate(animalNode.path("ENTRNC_DATE").asText()); // 입양 날짜
                animal.setBreeds(animalNode.path("BREEDS").asText()); // 품종
                animal.setAge(animalNode.path("AGE").asText()); // 나이
                animal.setSpcs(animalNode.path("SPCS").asText()); // 특성

                animals.add(animal);
            }

            apiRepository.saveAll(animals);
            log.info("Saved Seoul animals count: " + animals.size());
        } catch (IOException e) {
            log.error("Error saving animals from Seoul API: " + e.getMessage(), e);
        }
    }

    public List<ApiEntity> getAllAnimals() {
        try {
            return apiRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all animals: " + e.getMessage(), e);
            throw e; // or return an empty list, depending on your error handling strategy
        }
    }

    public ApiEntity getAnimalDetails(Long animalId) {
        // 서울과 경기 API에서 동물 정보를 가져오는 로직을 구현합니다.
        // 동물 ID를 기준으로 서울과 경기 데이터를 검색하여 반환합니다.
        Optional<ApiEntity> animalOptional = apiRepository.findById(animalId);

        if (animalOptional.isPresent()) {
            return animalOptional.get();
        } else {
            log.warn("Animal with ID " + animalId + " not found");
            return null;
        }
    }

    private ApiEntity convertJsonToAnimal(JsonNode itemNode) {
        ApiEntity animal = new ApiEntity();
        animal.setAbdmIdntfyNo(itemNode.path("ABDM_IDNTFY_NO").asText());
        animal.setSigunCd(itemNode.path("SIGUN_CD").asText());
        animal.setSigunNm(itemNode.path("SIGUN_NM").asText());
        animal.setThumbImageCours(itemNode.path("THUMB_IMAGE_COURS").asText());
        animal.setReceptDe(itemNode.path("RECEPT_DE").asText());
        animal.setDiscvryPlcInfo(itemNode.path("DISCVRY_PLC_INFO").asText());
        animal.setSpeciesNm(itemNode.path("SPECIES_NM").asText());
        animal.setColorNm(itemNode.path("COLOR_NM").asText());
        animal.setAgeInfo(itemNode.path("AGE_INFO").asText());
        animal.setPblancIdntfyNo(itemNode.path("PBLANC_IDNTFY_NO").asText());
        animal.setPblancBeginDe(itemNode.path("PBLANC_BEGIN_DE").asText());
        animal.setPblancEndDe(itemNode.path("PBLANC_END_DE").asText());
        animal.setImageCours(itemNode.path("IMAGE_COURS").asText());
        animal.setStateNm(itemNode.path("STATE_NM").asText());
        animal.setSexNm(itemNode.path("SEX_NM").asText());
        animal.setNeutYn(itemNode.path("NEUT_YN").asText());
        animal.setSfetrInfo(itemNode.path("SFETR_INFO").asText());
        animal.setShterNm(itemNode.path("SHTER_NM").asText());
        animal.setShterTelno(itemNode.path("SHTER_TELNO").asText());
        animal.setProtectPlc(itemNode.path("PROTECT_PLC").asText());
        animal.setJurisdInstNm(itemNode.path("JURISD_INST_NM").asText());
        animal.setRefineLotnoAddr(itemNode.path("REFINE_LOTNO_ADDR").asText());
        animal.setRefineZipCd(itemNode.path("REFINE_ZIP_CD").asText());

        return animal;
    }

    // 검색 필터링
    public List<ApiEntity> getSeoulAnimals() {
        return apiRepository.findAll().stream()
                .filter(animal -> animal.getNm() != null && animal.getNm().contains("센터"))
                .collect(Collectors.toList());
    }

    public List<ApiEntity> getGyeonggiAnimals() {
        return apiRepository.findAll().stream()
                .filter(animal -> animal.getProtectPlc() != null && animal.getProtectPlc().contains("경기"))
                .collect(Collectors.toList());
    }
}
