package com.example.webview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.BreakIterator;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ClipboardManager clipboardManager;
    ClipData clipData;
    SwipeRefreshLayout reloadSwipe;
    public static TextView url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webView);

/*
        Uri address = Uri.parse("https://sber.ru");
        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);

        if (openLinkIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openLinkIntent);
        } else {
            Log.d("Intent", "Не получается обработать намерение!");
        }*/



//  чтобы открывать ссылки в своей программе, нужно переопределить класс WebViewClient и позволить нашему приложению обрабатывать ссылки
        webView.setWebViewClient(new MyWebViewClient());
//        Теперь в нашем приложении создан WebViewClient, который позволяет загружать любой указанный URL, выбранный в WebView, в сам контейнер WebView, а не запускать браузер.
        webView.getSettings().setJavaScriptEnabled(true);

        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);

        Button openURL = findViewById(R.id.openURL_click);
        url = findViewById(R.id.url);
        Button back = findViewById(R.id.back);


        openURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // отдельный поток для загрузки страницы
                new Thread(new Runnable() {
                    public void run() {
                        try{
                            String content = getContent(url.getText().toString());
                            webView.post(new Runnable() {
                                public void run() {
                                    webView.loadDataWithBaseURL(url.getText().toString(),content, "text/html", "UTF-8", url.getText().toString());
                                    Toast.makeText(getApplicationContext(), "Страница загружена", Toast.LENGTH_SHORT).show();
                                }
                            });
                            webView.setVisibility(View.VISIBLE);
                        }
                        catch (IOException ex){
                            webView.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Ошибка загрузки страницы", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("about:blank");
            }
        });


    }

//    для загрузки определен метод getContent(), который будет загружать веб-страницу
//    с помощью класса HttpsURLConnection и возвращать код загруженной страницы в виде строки.
    private String getContent(String path) throws IOException {
        BufferedReader reader=null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            // Вначале создается элемент HttpsURLConnection:
            URL url=new URL(path);
            connection =(HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET"); // установка метода получения данных -GET
            connection.setReadTimeout(10000); // установка таймаута перед выполнением - 10 000 миллисекунд
            connection.connect();  // подключаемся к ресурсу
            // После подключение происходит считывание со входного потока:
            stream = connection.getInputStream();
            reader= new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf=new StringBuilder();
            String line;
            while ((line=reader.readLine()) != null) {
                buf.append(line).append("\n");
            }
            return(buf.toString());
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}