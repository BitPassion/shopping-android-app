package org.nearbyshops.enduserappnew.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.nearbyshops.enduserappnew.API.ShopService;
import org.nearbyshops.enduserappnew.DaggerComponentBuilder;
import org.nearbyshops.enduserappnew.Model.ModelRoles.User;
import org.nearbyshops.enduserappnew.Model.Shop;
import org.nearbyshops.enduserappnew.Preferences.PrefLocation;
import org.nearbyshops.enduserappnew.Preferences.PrefLogin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelShop extends AndroidViewModel {

    private MutableLiveData<List<Object>> datasetLive;
    private List<Object> dataset;


    private MutableLiveData<Shop> shopLive;
    private Shop shop;

    private MutableLiveData<Integer> event;
    private MutableLiveData<String> message;




    public static int EVENT_BECOME_A_SELLER_SUCCESSFUL = 1;
    public static int EVENT_SHOP_DETAILS_FETCHED = 2;
    public static int EVENT_SHOP_DELETED = 3;
    public static int EVENT_ = 20;
    public static int EVENT_NETWORK_FAILED = 21;





    @Inject
    Gson gson;


    @Inject
    ShopService shopService;







    public ViewModelShop(@NonNull Application application) {
        super(application);

        event = new MutableLiveData<>();
        message = new MutableLiveData<>();
        datasetLive = new MutableLiveData<>();
        dataset = new ArrayList<>();

        shopLive = new MutableLiveData<>();
        shop = new Shop();


        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);
    }





    public MutableLiveData<List<Object>> getData()
    {
        return datasetLive;
    }





    public LiveData<Integer> getEvent()
    {

        return event;
    }





    public LiveData<String> getMessage()
    {

        return message;
    }


    public MutableLiveData<Shop> getShopLive() {
        return shopLive;
    }





    public void becomeASeller()
    {

        Call<ResponseBody> call = shopService.becomeASeller(PrefLogin.getAuthorizationHeaders(getApplication()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200)
                {

                    User user = PrefLogin.getUser(getApplication());
                    user.setRole(User.ROLE_SHOP_ADMIN_CODE);
                    PrefLogin.saveUserProfile(user,getApplication());


                    message.postValue("Successful !");
                    event.postValue(ViewModelShop.EVENT_BECOME_A_SELLER_SUCCESSFUL);
                }
                else
                {
                    message.postValue("Failed Code : " + response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                message.postValue("Failed ! Check your network connection !");
            }
        });

    }




    public void getShopForShopStaff()
    {
        Call<Shop> call = shopService.getShopForShopStaff(
                PrefLogin.getAuthorizationHeaders(getApplication())
        );




        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {

                if(response.code()==200 && response.body()!=null)
                {
                    shopLive.postValue(response.body());
                    event.postValue(ViewModelShop.EVENT_SHOP_DETAILS_FETCHED);

                }
                else
                {
                    event.postValue(ViewModelShop.EVENT_NETWORK_FAILED);
                    message.postValue("Failed Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {

                event.postValue(ViewModelShop.EVENT_NETWORK_FAILED);
            }
        });
    }






    public void makeNetworkCallShop(int shopID)
    {
        Call<Shop> call = shopService.getShopDetails(
                shopID,
                PrefLocation.getLatitude(getApplication()),
                PrefLocation.getLongitude(getApplication())
        );


        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {

                if(response.code()==200 && response.body()!=null)
                {
//                    order.setShop(response.body());
//                    adapter.notifyItemChanged(0);


                    shopLive.postValue(response.body());
                    event.postValue(ViewModelShop.EVENT_SHOP_DETAILS_FETCHED);

                }
                else
                {
                    event.postValue(ViewModelShop.EVENT_NETWORK_FAILED);
                    message.postValue("Failed Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {

                event.postValue(ViewModelShop.EVENT_NETWORK_FAILED);
            }
        });
    }







    public void deleteShop(int shopID)
    {

        Call<ResponseBody> call = shopService.deleteShop(
                PrefLogin.getAuthorizationHeaders(getApplication()),
                shopID
        );



        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200)
                {
                    event.postValue(ViewModelShop.EVENT_SHOP_DELETED);
                    message.postValue("Successful !");
                }
                else
                {
                    event.postValue(ViewModelShop.EVENT_NETWORK_FAILED);
                    message.postValue("Failed Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                event.postValue(ViewModelShop.EVENT_NETWORK_FAILED);
                message.postValue("Network Failed ! ");

            }
        });


    }


}


