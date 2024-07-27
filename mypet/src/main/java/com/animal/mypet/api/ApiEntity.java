package com.animal.mypet.api;

import java.util.HashSet;
import java.util.Set;

import com.animal.mypet.animal.AnimalLike;
import com.animal.mypet.animal.AnimalLikeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Animal")
public class ApiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 경기동물 API 관련 필드
    @Column(name = "ABDM_IDNTFY_NO")
    private String abdmIdntfyNo; // 등록번호
    @Column(name = "SIGUN_CD")
    private String sigunCd;
    @Column(name = "SIGUN_NM")
    private String sigunNm;
    @Column(name = "THUMB_IMAGE_COURS")
    private String thumbImageCours;
    @Column(name = "RECEPT_DE")
    private String receptDe;
    @Column(name = "DISCVRY_PLC_INFO")
    private String discvryPlcInfo;
    @Column(name = "SPECIES_NM")
    private String speciesNm;
    @Column(name = "COLOR_NM")
    private String colorNm;
    @Column(name = "AGE_INFO")
    private String ageInfo;
    @Column(name = "PBLANC_IDNTFY_NO")
    private String pblancIdntfyNo;
    @Column(name = "PBLANC_BEGIN_DE")
    private String pblancBeginDe;
    @Column(name = "PBLANC_END_DE")
    private String pblancEndDe;
    @Column(name = "IMAGE_COURS")
    private String imageCours;
    @Column(name = "STATE_NM")
    private String stateNm;
    @Column(name = "SEX_NM")
    private String sexNm;
    @Column(name = "NEUT_YN")
    private String neutYn;
    @Column(name = "SFETR_INFO")
    private String sfetrInfo;
    @Column(name = "SHTER_NM")
    private String shterNm;
    @Column(name = "SHTER_TELNO")
    private String shterTelno;
    @Column(name = "PROTECT_PLC")
    private String protectPlc;
    @Column(name = "JURISD_INST_NM")
    private String jurisdInstNm;
    @Column(name = "REFINE_LOTNO_ADDR")
    private String refineLotnoAddr;
    @Column(name = "REFINE_ROADNM_ADDR")
    private String refineRoadnmAddr;
    @Column(name = "REFINE_ZIP_CD")
    private String refineZipCd;

    // 서울동물 API 관련 필드
    @Column(name = "ANIMAL_NO")
    private String animalNo;
    @Column(name = "NM")
    private String nm;
    @Column(name = "ENTRNC_DATE")
    private String entrncDate;
    @Column(name = "SPCS")
    private String spcs;
    @Column(name = "BREEDS")
    private String breeds;
    @Column(name = "SEXDSTN")
    private String sexdstn;
    @Column(name = "AGE")
    private String age;
    @Column(name = "ADP_STTUS")
    private String adpSttus;
    @Column(name = "TMPR_PRTC_STTUS")
    private String tmprPrtcSttus;
    @Column(name = "INTRCN_MVP_URL")
    private String intrcnMvpUrl;
    @Column(name = "INTRCN_CN", columnDefinition = "LONGTEXT")
    private String intrcnCn;

    // 서울시 유기동물 사진 API
    @Column(name = "PHOTO_URL")
    private String photoUrl;
    @Column(name = "PHOTO_NO")
    private String photoNo;
    
    // 이거 추가하면 동물검색 안댐 ;; 
//    @OneToMany(mappedBy = "animal")
//    private Set<AnimalLike> animalLikes = new HashSet<>();

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", abdmIdntfyNo='" + abdmIdntfyNo + '\'' +
                ", sigunCd='" + sigunCd + '\'' +
                ", sigunNm='" + sigunNm + '\'' +
                ", thumbImageCours='" + thumbImageCours + '\'' +
                ", receptDe='" + receptDe + '\'' +
                ", discvryPlcInfo='" + discvryPlcInfo + '\'' +
                ", speciesNm='" + speciesNm + '\'' +
                ", colorNm='" + colorNm + '\'' +
                ", ageInfo='" + ageInfo + '\'' +
                ", pblancIdntfyNo='" + pblancIdntfyNo + '\'' +
                ", pblancBeginDe='" + pblancBeginDe + '\'' +
                ", pblancEndDe='" + pblancEndDe + '\'' +
                ", imageCours='" + imageCours + '\'' +
                ", stateNm='" + stateNm + '\'' +
                ", sexNm='" + sexNm + '\'' +
                ", neutYn='" + neutYn + '\'' +
                ", sfetrInfo='" + sfetrInfo + '\'' +
                ", shterNm='" + shterNm + '\'' +
                ", shterTelno='" + shterTelno + '\'' +
                ", protectPlc='" + protectPlc + '\'' +
                ", jurisdInstNm='" + jurisdInstNm + '\'' +
                ", refineLotnoAddr='" + refineLotnoAddr + '\'' +
                ", refineRoadnmAddr='" + refineRoadnmAddr + '\'' +
                ", refineZipCd='" + refineZipCd + '\'' +
                ", animalNo='" + animalNo + '\'' +
                ", nm='" + nm + '\'' +
                ", entrncDate='" + entrncDate + '\'' +
                ", spcs='" + spcs + '\'' +
                ", breeds='" + breeds + '\'' +
                ", sexdstn='" + sexdstn + '\'' +
                ", age='" + age + '\'' +
                ", adpSttus='" + adpSttus + '\'' +
                ", tmprPrtcSttus='" + tmprPrtcSttus + '\'' +
                ", intrcnMvpUrl='" + intrcnMvpUrl + '\'' +
                ", intrcnCn='" + intrcnCn + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", photoNo='" + photoNo + '\'' +
                '}';
    }
}
