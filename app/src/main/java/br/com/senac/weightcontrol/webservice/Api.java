package br.com.senac.weightcontrol.webservice;

public class Api {
    private static final String ROOT_URL = "https://www.irmy.com.br/prova/weightcontrol/BancoApi/v1/Api.php?apicall=getweightcontrol";

    public static final String URL_CREATE_WEIGHTAPP = ROOT_URL + "createweightapp";
    public static final String URL_READ_WEIGHTAPP= ROOT_URL + "getweightapp";
    public static final String URL_UPDATE_WEIGHTAPP= ROOT_URL + "updateweightapp";
    public static final String URL_DELETE_WEIGHTAPP = ROOT_URL + "DELETEWEIGHTAPP&id=";
}
