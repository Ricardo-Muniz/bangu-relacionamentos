package br.com.bangu_ao_vivo.bangu.Utils;

import br.com.bangu_ao_vivo.bangu.Notifications.MyResponse;
import br.com.bangu_ao_vivo.bangu.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Toshiba pc on 15/03/2019.
 */

public interface ApiServiceNotifications {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAn65JcsY:APA91bH5EqcOOD15Iq5ngr4GpArwTQ9f2QulHlU0fzYtv6f-eefwmCK8OgWFR97Fyx02uavJlvcL8d50Lhij605dglkXmwMan7-2URidyD1yELvIn2iiAZEHtmIPQ5GeKNg4Dnj1zZiZ"

    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}


