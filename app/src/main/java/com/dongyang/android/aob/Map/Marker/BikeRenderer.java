package com.dongyang.android.aob.Map.Marker;

import android.content.Context;
import android.view.View;


import com.dongyang.android.aob.Map.Model.Bike.BikeItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class BikeRenderer extends DefaultClusterRenderer<BikeItem> {

    private View bikeMarkerLayout;

    public BikeRenderer(Context context, GoogleMap map, ClusterManager<BikeItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(BikeItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getIcon()));
        markerOptions.snippet(item.getPosition().toString());
        markerOptions.title(item.getTitle());
    }

    @Override
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<BikeItem> listener) {
        super.setOnClusterItemClickListener(listener);
    }


}


