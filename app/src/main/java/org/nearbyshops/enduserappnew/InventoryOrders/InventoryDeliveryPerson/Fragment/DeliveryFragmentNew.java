package org.nearbyshops.enduserappnew.InventoryOrders.InventoryDeliveryPerson.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.nearbyshops.enduserappnew.API.OrderService;
import org.nearbyshops.enduserappnew.API.OrderServiceDeliveryPersonSelf;
import org.nearbyshops.enduserappnew.DaggerComponentBuilder;
import org.nearbyshops.enduserappnew.Interfaces.NotifyLocation;
import org.nearbyshops.enduserappnew.Interfaces.NotifySearch;
import org.nearbyshops.enduserappnew.Interfaces.NotifySort;
import org.nearbyshops.enduserappnew.Interfaces.NotifyTitleChangedNew;
import org.nearbyshops.enduserappnew.Interfaces.RefreshAdjacentFragment;
import org.nearbyshops.enduserappnew.Interfaces.RefreshFragment;
import org.nearbyshops.enduserappnew.InventoryOrders.DeliverySlot.Model.DeliverySlot;
import org.nearbyshops.enduserappnew.InventoryOrders.DeliverySlot.ViewHolderDeliverySlot;
import org.nearbyshops.enduserappnew.InventoryOrders.OrderFilters.ShopFilter.ViewHolderFilterShop;
import org.nearbyshops.enduserappnew.InventoryOrders.OrderFilters.ViewHolderFilterOrders;
import org.nearbyshops.enduserappnew.Model.ModelCartOrder.Order;
import org.nearbyshops.enduserappnew.Model.ModelEndPoints.OrderEndPoint;
import org.nearbyshops.enduserappnew.Model.ModelRoles.User;
import org.nearbyshops.enduserappnew.Model.ModelStatusCodes.OrderStatusHomeDelivery;
import org.nearbyshops.enduserappnew.Preferences.PrefLocation;
import org.nearbyshops.enduserappnew.Preferences.PrefLogin;
import org.nearbyshops.enduserappnew.R;
import org.nearbyshops.enduserappnew.Utility.UtilityFunctions;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHolderFilters.Models.FilterOrders;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersCommon.Models.EmptyScreenDataFullScreen;
import org.nearbyshops.enduserappnew.ViewHolders.ViewHoldersOrders.ViewHolderOrderDeliveryGuy;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeliveryFragmentNew extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ViewHolderOrderDeliveryGuy.ListItemClick,
        ViewHolderDeliverySlot.ListItemClick,
        ViewHolderFilterShop.ListItemClick,
        ViewHolderFilterOrders.ListItemClick,
        NotifySort, NotifySearch, RefreshFragment, NotifyLocation {




    public static String SCREEN_MODE_INTENT_KEY = "screen_mode_intent_key";


    public static int SCREEN_MODE_MARKET_ADMIN = 1;
    public static int SCREEN_MODE_DELIVERY_PERSON_MARKET = 2;
    public static int SCREEN_MODE_DELIVERY_PERSON_VENDOR = 3;

    private int screenMode = SCREEN_MODE_DELIVERY_PERSON_MARKET;




    @Inject
    OrderService orderService;

    @Inject
    OrderServiceDeliveryPersonSelf orderServiceDelivery;


    private RecyclerView recyclerView;
    private Adapter adapter;

    public List<Object> dataset = new ArrayList<>();


    private SwipeRefreshLayout swipeContainer;


    final private int limit = 5;
    private int offset = 0;
    private int item_count = 0;


    private boolean isDestroyed;



    Date selectedDate;
    Integer deliverySlotID;
    Integer shopID;
    boolean isASAPDelivery = false;




    public DeliveryFragmentNew() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);

    }





    public static DeliveryFragmentNew newInstance(int orderStatus) {
        DeliveryFragmentNew fragment = new DeliveryFragmentNew();
        Bundle args = new Bundle();
        args.putInt("order_status",orderStatus);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_inventory_delivery_guy, container, false);


        recyclerView = rootView.findViewById(R.id.recyclerView);
        swipeContainer = rootView.findViewById(R.id.swipeContainer);


        if(savedInstanceState==null)
        {
            makeRefreshNetworkCall();
        }




        if(getActivity()!=null)
        {
            screenMode = getActivity().getIntent().getIntExtra(SCREEN_MODE_INTENT_KEY,SCREEN_MODE_DELIVERY_PERSON_MARKET);
        }





        setupRecyclerView();
        setupSwipeContainer();


        return rootView;
    }






    private void setupSwipeContainer()
    {
        if(swipeContainer!=null) {

            swipeContainer.setOnRefreshListener(this);
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

    }






    private void setupRecyclerView()
    {


        int orderStatus = getArguments().getInt("order_status");

        adapter = new Adapter(dataset,this,screenMode);
        recyclerView.setAdapter(adapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if(offset + limit > layoutManager.findLastVisibleItemPosition() + 1 - 1)
                {
                    return;
                }


                if(layoutManager.findLastVisibleItemPosition()==dataset.size()-1 + 1)
                {
                    // trigger fetch next page

//                    if(layoutManager.findLastVisibleItemPosition() == previous_position)
//                    {
//                        return;
//                    }


                    if((offset+limit)<=item_count)
                    {
                        offset = offset + limit;
                        makeNetworkCall(false);
                    }

//                    previous_position = layoutManager.findLastVisibleItemPosition();

                }

            }
        });
    }



    @Override
    public void onRefresh() {

        offset = 0;
        makeNetworkCall(true);
    }




    private void makeRefreshNetworkCall()
    {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);

                onRefresh();
            }
        });
    }





    private void makeNetworkCall(final boolean clearDataset)
    {
        User user = PrefLogin.getUser(getActivity());


        String current_sort = "";
//        current_sort = PrefSortOrders.getSort(getContext()) + " " + PrefSortOrders.getAscending(getContext());


        int orderStatus = getArguments().getInt("order_status");




        Integer orderStatusHD = null;
        orderStatusHD = orderStatus;



        Integer deliveryGuyID = null;
        deliveryGuyID = user.getUserID();

        boolean isDeliveryGuyNull = false;
        Integer HDStatusLessThan = null;


        boolean getFilterShops = false;
        boolean getFilterDeliveryPerson = false;



        int deliveryMode = -1;

        if(screenMode==SCREEN_MODE_DELIVERY_PERSON_MARKET)
        {
            deliveryMode=Order.DELIVERY_MODE_HOME_DELIVERY;
            getFilterShops = clearDataset; // fetch once and stop on pagination

        }
        else if(screenMode==SCREEN_MODE_MARKET_ADMIN)
        {
            deliveryMode=Order.DELIVERY_MODE_HOME_DELIVERY;
            getFilterShops=clearDataset;


            deliveryGuyID=null;
            getFilterDeliveryPerson=clearDataset; // fetch once and stop  on Pagination

        }
        else if(screenMode==SCREEN_MODE_DELIVERY_PERSON_VENDOR)
        {
            deliveryMode=Order.DELIVERY_MODE_HOME_DELIVERY;
        }






        current_sort= " pickup_distance ";



        if(orderStatus== OrderStatusHomeDelivery.DELIVERY_BOY_UNASSIGNED)
        {
            // order status of home delivery order is order packed then do not filter using delivery ID
            deliveryGuyID=null;
            isDeliveryGuyNull=true;
            HDStatusLessThan=OrderStatusHomeDelivery.ORDER_PACKED;

            orderStatusHD=null;

        }
        else if(orderStatus==OrderStatusHomeDelivery.DELIVERY_BOY_ASSIGNED)
        {
            HDStatusLessThan=OrderStatusHomeDelivery.ORDER_PACKED;
            orderStatusHD=null;

        }
        else if(orderStatus==OrderStatusHomeDelivery.OUT_FOR_DELIVERY)
        {
            current_sort= " delivery_distance ";
        }







        Call<OrderEndPoint> call = orderService.getOrdersForDelivery(
                    PrefLogin.getAuthorizationHeaders(getActivity()),
                    selectedDate,
                    isASAPDelivery,
                deliverySlotID,
                shopID,


                    isDeliveryGuyNull,
                    deliveryGuyID,

                    null,

                    HDStatusLessThan,
                    orderStatusHD,

                PrefLocation.getLatitude(getActivity()), PrefLocation.getLongitude(getActivity()),
                    getFilterDeliveryPerson,
                    getFilterShops,
                clearDataset,

                    searchQuery,
                current_sort,
                limit,offset,
                clearDataset,false
        );


        boolean finalGetFilterShops = getFilterShops;
        boolean finalGetFilterDeliveryPerson = getFilterDeliveryPerson;
        call.enqueue(new Callback<OrderEndPoint>() {
            @Override
            public void onResponse(Call<OrderEndPoint> call, Response<OrderEndPoint> response) {

                if(isDestroyed)
                {
                    return;
                }

                if(response.code()==200)
                {


                    if(clearDataset)
                    {



                        item_count = response.body().getItemCount();
                        dataset.clear();

                        FilterOrders filterOrders = new FilterOrders();
                        filterOrders.setShowDeliverySlotFilter(true);

                        filterOrders.setShowShopFilter(finalGetFilterShops);
                        filterOrders.setShowDeliveryGuyFilter(finalGetFilterDeliveryPerson);

                        filterOrders.setSelectedDeliverySlotID(deliverySlotID);
                        filterOrders.setSelectedShopID(shopID);

                        filterOrders.setOrderEndPoint(response.body());


                        dataset.add(filterOrders);
                    }



                    if(response.body().getResults()!=null)
                    {
                        dataset.addAll(response.body().getResults());
                    }


//                    adapter.notifyDataSetChanged();
                    notifyTitleChanged();



                    if(offset+limit >= item_count)
                    {
                        adapter.setLoadMore(false);
                    }
                    else
                    {
                        adapter.setLoadMore(true);
                    }

                }
                else
                {
                    showToastMessage("Failed Code : "  + String.valueOf(response.code()));
                }


                swipeContainer.setRefreshing(false);






                if(item_count==0)
                {
                    dataset.add(EmptyScreenDataFullScreen.emptyScreenPFSINventory());
                }


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<OrderEndPoint> call, Throwable t) {

                if(isDestroyed)
                {
                    return;
                }

//                showToastMessage("Network Request failed !");
                dataset.clear();
                dataset.add(EmptyScreenDataFullScreen.getOffline());
                adapter.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
            }
        });
    }





    @Override
    public void onResume() {
        super.onResume();
        notifyTitleChanged();
        isDestroyed=false;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed=true;
        ViewHolderFilterOrders.clearDateStatic(getActivity());
    }





    private void showToastMessage(String message)
    {
//        if(getActivity()!=null)
//        {
//            Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
//        }

        UtilityFunctions.showToastMessage(getActivity(),message);
    }





    private void notifyTitleChanged()
    {

        if(getActivity() instanceof NotifyTitleChangedNew)
        {
            ((NotifyTitleChangedNew)getActivity())
                    .NotifyTitleChanged(
                            "(" + dataset.size()
                                    + "/" + item_count + ")");

        }
    }





    // Refresh the Confirmed PlaceholderFragment

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }




    @Override
    public void notifyOrderSelected(Order order) {

//        PrefOrderDetail.saveOrder(order,getActivity());
//        getActivity().startActivity(new Intent(getActivity(), OrderDetail.class));
    }




    @Override
    public void statusUpdateSuccessful(Order order, int position) {

        refreshNeighboringFragment();
        dataset.remove(order);
        item_count = item_count - 1;
        adapter.notifyItemRemoved(position);
        notifyTitleChanged();
    }




    private void refreshNeighboringFragment()
    {

        if(getActivity() instanceof RefreshAdjacentFragment)
        {
            ((RefreshAdjacentFragment) getActivity()).refreshAdjacentFragment();
        }
    }





    
    @Override
    public void notifySortChanged() {
        makeRefreshNetworkCall();
    }






    private String searchQuery = null;

    @Override
    public void search(final String searchString) {
        searchQuery = searchString;
        makeRefreshNetworkCall();
    }

    @Override
    public void endSearchMode() {
        searchQuery = null;
        makeRefreshNetworkCall();
    }


    

    @Override
    public void refreshFragment() {
        makeRefreshNetworkCall();
    }




    @Override
    public void fetchedLocation(Location location) {
        makeRefreshNetworkCall();
    }




    @Override
    public void listItemClick(int deliverySlotID) {

        if(deliverySlotID==-1)
        {
            this.deliverySlotID=null;
        }
        else
        {
            this.deliverySlotID=deliverySlotID;
        }

        makeRefreshNetworkCall();
    }




    @Override
    public void editDeliverySlot(DeliverySlot deliverySlot, int position) {

    }

    @Override
    public void removeDeliverySlot(DeliverySlot deliverySlot, int position) {

    }




    @Override
    public void filtersUpdated(int deliverySlotID, int shopID, int deliveryBoyID, int date) {



        if(deliverySlotID==-1)
        {
            this.deliverySlotID=null;
        }
        else
        {
            this.deliverySlotID=deliverySlotID;
        }


        if(shopID==-1)
        {
            this.shopID=null;
        }
        else
        {
            this.shopID=shopID;
        }




        if(date == ViewHolderFilterOrders.FILTER_BY_DATE_ASAP)
        {
            selectedDate=null;
            isASAPDelivery=true;
        }
        else if(date ==ViewHolderFilterOrders.FILTERS_CLEARED)
        {
            selectedDate=null;
            isASAPDelivery=false;
        }
        else if(date == ViewHolderFilterOrders.FILTER_BY_DATE_TODAY)
        {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);

            selectedDate = new Date(calendar.getTimeInMillis());
            isASAPDelivery=false;
        }
        else if(date ==ViewHolderFilterOrders.FILTER_BY_DATE_TOMORROW)
        {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.add(Calendar.DATE,1);

            selectedDate = new Date(calendar.getTimeInMillis());
            isASAPDelivery=false;
        }



        makeRefreshNetworkCall();
    }



    @Override
    public void listItemFilterShopClick(int shopID) {


        if(shopID==-1)
        {
            this.shopID=null;
        }
        else
        {
            this.shopID=shopID;
        }

        makeRefreshNetworkCall();
    }


}
