package org.nearbyshops.enduserappnew.ViewHolders.ViewHolderUserProfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.nearbyshops.enduserappnew.EditDataScreens.EditShop.EditShop;
import org.nearbyshops.enduserappnew.EditDataScreens.EditShop.EditShopFragment;
import org.nearbyshops.enduserappnew.Model.ModelRoles.User;
import org.nearbyshops.enduserappnew.Model.ModelServiceConfig.ServiceConfigurationLocal;
import org.nearbyshops.enduserappnew.Model.Shop;
import org.nearbyshops.enduserappnew.MyApplication;
import org.nearbyshops.enduserappnew.Preferences.PrefLogin;
import org.nearbyshops.enduserappnew.Preferences.PrefServiceConfig;
import org.nearbyshops.enduserappnew.Preferences.PrefShopAdminHome;
import org.nearbyshops.enduserappnew.R;
import org.nearbyshops.enduserappnew.ViewModels.ViewModelShop;
import org.nearbyshops.enduserappnew.aSellerModule.DashboardShopStaff.ShopDashboardForStaff;
import org.nearbyshops.enduserappnew.aSellerModule.DashboardDeliveryGuy.DeliveryHome;
import org.nearbyshops.enduserappnew.aSellerModule.DashboardShopAdmin.ShopAdminHome;
import org.nearbyshops.enduserappnew.aSellerModule.InventoryDeliveryPerson.DeliveryGuyDashboard;
import org.nearbyshops.enduserappnew.adminModule.DashboardAdmin.AdminDashboard;
import org.nearbyshops.enduserappnew.adminModule.DashboardStaff.StaffDashboard;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewHolderRoleDashboard extends RecyclerView.ViewHolder{






    @BindView(R.id.dashboard_name) TextView dashboardName;
    @BindView(R.id.market_name) TextView marketName;
    @BindView(R.id.dashboard_description) TextView dashboardDescription;




    private Context context;
    private Fragment fragment;
    private ViewModelShop viewModelShop;


    private ProgressDialog progressDialog;





    public static ViewHolderRoleDashboard create(ViewGroup parent, Context context, Fragment fragment)
    {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_role_dashboard,parent,false);
        return new ViewHolderRoleDashboard(view,context,fragment);
    }





    public ViewHolderRoleDashboard(View itemView, Context context, Fragment fragment) {
        super(itemView);

        ButterKnife.bind(this,itemView);
        this.context = context;
        this.fragment = fragment;


        bindDashboard();



//        viewModelShop = ViewModelProviders.of(fragment).get(ViewModelShop.class);

        viewModelShop = new ViewModelShop(MyApplication.application);


        viewModelShop.getShopLive().observe(fragment, new Observer<Shop>() {
            @Override
            public void onChanged(Shop shop) {

                PrefShopAdminHome.saveShop(shop,context);

                Intent intent = new Intent(context, ShopDashboardForStaff.class);
                context.startActivity(intent);
            }
        });



        viewModelShop.getEvent().observe(fragment, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if(progressDialog!=null)
                {
                    progressDialog.dismiss();
                }



                if(integer == ViewModelShop.EVENT_BECOME_A_SELLER_SUCCESSFUL)
                {
                    bindDashboard();
                }

            }
        });




        viewModelShop.getMessage().observe(fragment, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showToastMessage(s);

                if(progressDialog!=null)
                {
                    progressDialog.dismiss();
                }
            }
        });


    }






    @OnClick(R.id.dashboard_by_role)
    public void dashboardClick()
    {

        User user = PrefLogin.getUser(context);

        if(user==null)
        {
            return;
        }

        if(user.getRole()==User.ROLE_SHOP_ADMIN_CODE)
        {

            Intent intent = new Intent(context, ShopAdminHome.class);
            context.startActivity(intent);
        }
        else if(user.getRole()==User.ROLE_SHOP_STAFF_CODE)
        {

            viewModelShop.getShopForShopStaff();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait ... getting shop details !");
            progressDialog.show();

        }
        else if(user.getRole()==User.ROLE_ADMIN_CODE)
        {
            Intent intent = new Intent(context, AdminDashboard.class);
            context.startActivity(intent);
        }
        else if(user.getRole()==User.ROLE_STAFF_CODE)
        {
            Intent intent = new Intent(context, StaffDashboard.class);
            context.startActivity(intent);
        }
        else if(user.getRole()==User.ROLE_DELIVERY_GUY_SELF_CODE)
        {
//            Intent intent = new Intent(context, DeliveryHome.class);
//            context.startActivity(intent);


            Intent deliveryGuyDashboard = new Intent(context, DeliveryGuyDashboard.class);
            context.startActivity(deliveryGuyDashboard);
        }
        else if(user.getRole()==User.ROLE_END_USER_CODE)
        {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setTitle("Confirm Become a Seller !")
                    .setMessage("Do you want to Create your Shop and become a seller !")
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                            viewModelShop.becomeASeller();
//                            progressDialog = new ProgressDialog(context);
//                            progressDialog.setMessage("Please wait ... converting you to a seller !");
//                            progressDialog.show();


                            //     open edit shop in edit mode
                            Intent intent = new Intent(context, EditShop.class);
                            intent.putExtra(EditShopFragment.EDIT_MODE_INTENT_KEY, EditShopFragment.MODE_ADD);
//                            context.startActivity(intent);
                            fragment.startActivityForResult(intent,890);


                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            showToastMessage("Cancelled !");
                        }
                    })
                    .show();



        }

    }




    public void bindDashboard()
    {
        User user = PrefLogin.getUser(context);

        ServiceConfigurationLocal globalConfig = PrefServiceConfig.getServiceConfigLocal(context);

        if(user==null || globalConfig==null)
        {
            return;
        }


        String marketNameString = globalConfig.getServiceName() + ", " + globalConfig.getCity();

        if(user.getRole()==User.ROLE_SHOP_ADMIN_CODE)
        {
            marketName.setText(marketNameString);
            dashboardName.setText("Shop Dashboard");
            dashboardDescription.setText("Press here to access the shop dashboard !");
        }
        else if(user.getRole()==User.ROLE_SHOP_STAFF_CODE)
        {
            marketName.setText(marketNameString);
            dashboardName.setText("Shop Staff Dashboard");
            dashboardDescription.setText("Press here to access shop dashboard !");
        }
        else if(user.getRole()==User.ROLE_DELIVERY_GUY_SELF_CODE)
        {
            marketName.setText(marketNameString);
            dashboardName.setText("Delivery Dashboard");
            dashboardDescription.setText("Press here to access the Delivery dashboard !");
        }
        else if(user.getRole()==User.ROLE_ADMIN_CODE)
        {
            marketName.setText(marketNameString);
            dashboardName.setText("Admin Dashboard");
            dashboardDescription.setText("Press here to access the admin dashboard !");
        }
        else if(user.getRole()==User.ROLE_STAFF_CODE)
        {
            marketName.setText(marketNameString);
            dashboardName.setText("Staff Dashboard");
            dashboardDescription.setText("Press here to access the staff dashboard !");
        }
        else if(user.getRole()==User.ROLE_END_USER_CODE)
        {
            marketName.setText(marketNameString);

            dashboardName.setText("Become a Seller for " + marketNameString);
            dashboardDescription.setText(" ... Press here to create your shop !");
        }

    }




    void listItemClick()
    {
        ((ListItemClick)fragment).listItemClick();
    }




    private void showToastMessage(String message)
    {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }







    public interface ListItemClick
    {
        void listItemClick();
    }



}

