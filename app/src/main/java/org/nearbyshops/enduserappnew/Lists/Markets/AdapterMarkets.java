package org.nearbyshops.enduserappnew.Lists.Markets;



import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;

import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderMarket.ViewHolderCurrentMarket;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderMarket.ViewHolderMarket;
import org.nearbyshops.enduserappnew.Model.ModelRoles.User;
import org.nearbyshops.enduserappnew.Model.ModelServiceConfig.ServiceConfigurationGlobal;
import org.nearbyshops.enduserappnew.Model.ModelServiceConfig.ServiceConfigurationLocal;
import org.nearbyshops.enduserappnew.DaggerComponentBuilder;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderMarket.Model.MarketsList;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.Models.SignInMarker;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderUserProfile.Model.RoleDashboardMarker;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderUserProfile.ViewHolderRoleDashboard;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.LoadingViewHolder;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.Models.EmptyScreenDataListItem;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.Models.HeaderTitle;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.Models.SetLocationManually;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.ViewHolderEmptyScreenListItem;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.ViewHolderHeader;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.ViewHolderHorizontalList;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.ViewHolderSetLocationManually;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.ViewHolderSignIn;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderUserProfile.ViewHolderUserProfile;



import javax.inject.Inject;
import java.util.List;

/**
 * Created by sumeet on 13/6/16.
 */
public class AdapterMarkets extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataset = null;



    private static final int view_type_current_market = 1;
    private static final int view_type_saved_markets_list = 2;
    private static final int view_type_user_profile = 3;
    private static final int view_type_markets_header = 4;
    private static final int VIEW_TYPE_Market = 5;
    private static final int view_type_sign_in = 6;
    private static final int VIEW_TYPE_SCROLL_PROGRESS_BAR = 7;
    private static final int VIEW_TYPE_create_market = 8;
    private static final int view_type_role_dashboard = 9;
    public static final int VIEW_TYPE_SET_LOCATION_MANUALLY = 10;


    @Inject Gson gson;
    private Fragment fragment;
//    private int total_items_count;
    private boolean loadMore;



    public AdapterMarkets(List<Object> dataset, Fragment fragment) {

        this.dataset = dataset;
        this.fragment = fragment;


        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);
    }





    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        if(viewType == view_type_current_market)
        {

            return ViewHolderCurrentMarket.create(parent,fragment.getActivity());
        }
        else if(viewType==view_type_saved_markets_list)
        {
            return ViewHolderHorizontalList.create(parent,fragment.getActivity(),fragment);
        }
        else if(viewType == view_type_user_profile)
        {
            return ViewHolderUserProfile.create(parent,fragment.getActivity(),fragment);
        }
        else if(viewType == view_type_markets_header)
        {
            return ViewHolderHeader.createGreyBG(parent,fragment.getActivity());
        }
        else if(viewType == view_type_sign_in)
        {
            return ViewHolderSignIn.create(parent,fragment.getActivity(),fragment);
        }
        else if (viewType == VIEW_TYPE_Market) {


            return ViewHolderMarket.create(parent,fragment.getActivity(), fragment);
        }
        else if (viewType == VIEW_TYPE_SCROLL_PROGRESS_BAR) {


            return LoadingViewHolder.create(parent,fragment.getActivity());
        }
        else if(viewType==VIEW_TYPE_create_market)
        {
            return ViewHolderEmptyScreenListItem.create(parent,fragment.getActivity(),fragment);
        }
        else if(viewType ==view_type_role_dashboard)
        {
            return ViewHolderRoleDashboard.create(parent,fragment.getActivity(),fragment);
        }
        else if(viewType==VIEW_TYPE_SET_LOCATION_MANUALLY)
        {
            return ViewHolderSetLocationManually.create(parent,fragment.getActivity(),fragment);
        }




        return null;
    }


    @Override
    public int getItemViewType(int position) {

        super.getItemViewType(position);

        if (position == dataset.size()) {

            return VIEW_TYPE_SCROLL_PROGRESS_BAR;
        }
        else if(dataset.get(position) instanceof ServiceConfigurationLocal)
        {
            return view_type_current_market;
        }
        else if(dataset.get(position) instanceof MarketsList)
        {
            return view_type_saved_markets_list;
        }
        else if(dataset.get(position) instanceof SignInMarker)
        {
            return view_type_sign_in;
        }
        else if(dataset.get(position) instanceof User)
        {
            return view_type_user_profile;
        }
        else if(dataset.get(position) instanceof HeaderTitle)
        {
            return view_type_markets_header;
        }
        else if(dataset.get(position) instanceof ServiceConfigurationGlobal){

            return VIEW_TYPE_Market;
        }
        else if(dataset.get(position) instanceof EmptyScreenDataListItem)
        {

            return VIEW_TYPE_create_market;
        }
        else if(dataset.get(position) instanceof RoleDashboardMarker)
        {
            return view_type_role_dashboard;
        }
        else if(dataset.get(position) instanceof SetLocationManually)
        {
            return VIEW_TYPE_SET_LOCATION_MANUALLY;
        }



        return -1;
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderVH, int position) {


        if(holderVH instanceof ViewHolderCurrentMarket)
        {
            ViewHolderCurrentMarket holderCurrentMarket = (ViewHolderCurrentMarket) holderVH;
            holderCurrentMarket.setItem((ServiceConfigurationLocal) dataset.get(position));

        }
        else if(holderVH instanceof ViewHolderRoleDashboard)
        {
            ((ViewHolderRoleDashboard) holderVH).bindDashboard();
        }
        else if(holderVH instanceof ViewHolderHorizontalList)
        {
            MarketsList marketsList = (MarketsList) dataset.get(position);
            AdapterSavedMarkets adapter = new AdapterSavedMarkets(marketsList.getDataset(),fragment.getActivity(),fragment);
            ((ViewHolderHorizontalList) holderVH).setItem(adapter,"Favourite Markets");

        }
        else if(holderVH instanceof ViewHolderUserProfile)
        {
            ViewHolderUserProfile viewHolderUserProfile = (ViewHolderUserProfile) holderVH;
            viewHolderUserProfile.setItem((User) dataset.get(position));

        }
        else if (holderVH instanceof ViewHolderMarket) {


            ((ViewHolderMarket)holderVH).setItem((ServiceConfigurationGlobal) dataset.get(position));

        }
        else if(holderVH instanceof ViewHolderEmptyScreenListItem)
        {
            if(dataset.get(position) instanceof EmptyScreenDataListItem)
            {
                ((ViewHolderEmptyScreenListItem) holderVH).setItem((EmptyScreenDataListItem) dataset.get(position));
            }
        }
        else if(holderVH instanceof ViewHolderHeader)
        {
            ((ViewHolderHeader) holderVH).setItem((HeaderTitle) dataset.get(position));
        }
        else if (holderVH instanceof LoadingViewHolder) {

            ((LoadingViewHolder) holderVH).setLoading(loadMore);
        }


        //        else if(holderVH instanceof ViewHolderSavedMarketList)
//        {
//
//            ((ViewHolderSavedMarketList) holderVH).setItem((List<ServiceConfigurationGlobal>) dataset.get(position));
//
//        }
//        else if(holderVH instanceof ViewHolderSavedMarketList)
//        {
//
//            ((ViewHolderSavedMarketList) holderVH).setItem((MarketsList) dataset.get(position));
//
//        }

    }




    public void setLoadMore(boolean loadMore)
    {
        this.loadMore = loadMore;
    }




    public void setData(List<Object> data)
    {
        dataset = data;
    }




    @Override
    public int getItemCount() {

        return (dataset.size()+1);
    }



}
