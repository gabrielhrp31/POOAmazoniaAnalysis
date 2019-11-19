package tools;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;
import models.Region;
import models.Squad;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Firebase {
    private GoogleCredentials credentials;
    private FirebaseOptions options;
    private Firestore db;

    public Firebase() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("credentials.json");
        this.credentials = GoogleCredentials.fromStream(serviceAccount);
        this.options = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://poo-queimadas.firebaseio.com")
                .setCredentials(credentials)
                .build();

        FirebaseApp.initializeApp(options);
        this.db = FirestoreClient.getFirestore();
    }


    public void write(int qualSalvar, int id, String name, Boolean protectedArea, int squadResponsable, int quantityOfSoldiers, String regionResponsable) {
        DocumentReference docRef = null;
        Map<String, Object> data = new HashMap<>();
        String idString = " ";
        idString = Integer.toString(id);
        if (qualSalvar == 0) {//REGION
            docRef = db.collection("regions").document(idString);
            //Adiciona as informações ao documento com o id "nomeID" usando a hashMap
            //nome campo | valor
            data.put("name", name);
            data.put("protectedArea", protectedArea);
            data.put("squadResponsable", squadResponsable);
        }

        if (qualSalvar == 1) {//SQUAD
            docRef = db.collection("squads").document(idString);
            data.put("name", name);
            data.put("regionResponsable", regionResponsable);
            data.put("quantityOfSoldiers", quantityOfSoldiers);
        }


        // grava dados de forma assíncrona
        ApiFuture<WriteResult> result = docRef.set(data);

        // ...
        // result.get() blocks on response

        try {
            System.out.println("Hora da atualizacao: " + result.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public List<QueryDocumentSnapshot> read(int qualLer) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query=null;
        if(qualLer==0){
            query = db.collection("regions").get();
        }
        if(qualLer==1){
            query = db.collection("squads").get();
        }

        // AQUI É O DIRETORIO DO BANCO DE DADOS
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();//documents esta com os dados

    return documents;
    }
}