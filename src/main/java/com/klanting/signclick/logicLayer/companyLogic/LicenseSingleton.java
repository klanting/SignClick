package com.klanting.signclick.logicLayer.companyLogic;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import com.klanting.signclick.utils.Utils;


public class LicenseSingleton {
    private static LicenseSingleton instance = null;

    public LicenseManager getCurrentLicenses() {
        return currentLicenses;
    }

    public LicenseManager getLicenseRequests() {
        return licenseRequests;
    }

    private LicenseManager currentLicenses;
    private LicenseManager licenseRequests;

    public LicenseSingleton(){
        this.currentLicenses = new LicenseManager();
        this.licenseRequests = new LicenseManager();
    }

    public LicenseSingleton(JsonObject jsonObject, JsonDeserializationContext context){
        currentLicenses = context.deserialize(jsonObject.get("currentLicenses"), new TypeToken<LicenseManager>(){}.getType());
        licenseRequests = context.deserialize(jsonObject.get("licenseRequests"), new TypeToken<LicenseManager>(){}.getType());
    }

    public JsonObject toJson(JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("currentLicenses", context.serialize(currentLicenses));
        jsonObject.add("licenseRequests", context.serialize(licenseRequests));


        return jsonObject;
    }

    public static LicenseSingleton getInstance(){
        if (instance == null){
            instance = new LicenseSingleton();
        }

        return instance;
    }

    public static void Save(){

        if(instance == null){
            return;
        }

        Utils.writeSave("licenses", instance);
        instance = null;
    }

    public static void Restore(){

        instance = Utils.readSave("licenses", new TypeToken<LicenseSingleton>(){}.getType(), new LicenseSingleton());
    }

    public static void clear(){
        instance = null;
    }
}
