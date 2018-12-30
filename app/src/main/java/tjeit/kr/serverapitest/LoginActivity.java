package tjeit.kr.serverapitest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tjeit.kr.serverapitest.utils.PasswordUtil;

public class LoginActivity extends BaseActivity {

    private android.widget.EditText userIdEdt;
    private android.widget.EditText userPwEdt;
    private android.widget.Button loginBtn;
    private android.widget.TextView findPwTxt;
    private android.widget.TextView signUpTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        setupEvents();
        setValues();
    }

    @Override
    public void setupEvents() {

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivity(intent);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                로그인 버튼이 눌리면 서버에 아이디/비번 전달.
//                회원이 맞는지 아닌지 검증 받고 싶다.
//                서버 전달 : OkHttp 라이브러리 이용.

                OkHttpClient client = new OkHttpClient();

//                POST방식으로 첨부 예시.
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", userIdEdt.getText().toString())
                        .add("password", PasswordUtil.getEncrytedPassword(userPwEdt.getText().toString()))
                        .build();

//                서버에 요청을 담당하는 Request 클래스를 사용

                Request request = new Request.Builder()
                        .url("http://api-dev.lebit.kr/auth")
                        .post(requestBody)
                        .build();

//                client에게 request에 담긴 접속정보를 실행해달라고 요청.

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(mContext, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

//                        response 변수는 단 한번만 접근 허용.
//                        한 번 접근할 때 변수에 따로 저장.
                        String responseBody = response.body().string();
                        Log.d("응답내용", responseBody);

                        try {
                            JSONObject root = new JSONObject(responseBody);
                            int code = root.getInt("code");
                            String message = root.getString("message");
                            JSONObject data = root.getJSONObject("data");

                            String token = data.getString("token");
                            JSONObject user = data.getJSONObject("user");

                            int id = user.getInt("id");
                            String user_id = user.getString("user_id");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String phone = user.getString("phone");



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });


            }
        });

    }

    @Override
    public void setValues() {

    }

    @Override
    public void bindViews() {

        this.signUpTxt = (TextView) findViewById(R.id.signUpTxt);
        this.findPwTxt = (TextView) findViewById(R.id.findPwTxt);
        this.loginBtn = (Button) findViewById(R.id.loginBtn);
        this.userPwEdt = (EditText) findViewById(R.id.userPwEdt);
        this.userIdEdt = (EditText) findViewById(R.id.userIdEdt);
    }
}