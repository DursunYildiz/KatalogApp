package com.example.bitirme_projesi;

import java.util.ArrayList;

public class Model {
    String İsim;
    String Image;
    String İd;
    String Açıklama;
    String Tarih;
    String Kategory;

   
    String Image2;
    String Image3;
    String Image4;
    String Image5;



    public Model(String isim, String id, String image, String açıklama, String tarih, String kategory,String image2,String image3,String image4
            ,String image5){
        this.İsim=isim;
        this.İd=id;
        this.Tarih=tarih;
        this.Image=image;

        this.Kategory=kategory;
        this.Açıklama=açıklama;
        this.Image2=image2;
        this.Image3=image3;
        this.Image4=image4;
        this.Image5=image5;

    }

     public Model(){}



    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }

    public String getImage4() {
        return Image4;
    }

    public void setImage4(String image4) {
        Image4 = image4;
    }

    public String getImage5() {
        return Image5;
    }

    public void setImage5(String image5) {
        Image5 = image5;
    }

    public String getKategory() {
        return Kategory;
    }

    public void setKategory(String kategory) {
        Kategory = kategory;
    }

    public String getTarih() {
        return Tarih;
    }

    public void setTarih(String tarih) {
        Tarih = tarih;
    }

    public String getIsim() {
        return İsim;
    }

    public void setIsim(String isim) {
        İsim = isim;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getId() {
        return İd;
    }

    public void setId(String id) {
        İd = id;
    }

    public String getAçıklama() {
        return Açıklama;
    }

    public void setAçıklama(String açıklama) {
        Açıklama = açıklama;
    }
}