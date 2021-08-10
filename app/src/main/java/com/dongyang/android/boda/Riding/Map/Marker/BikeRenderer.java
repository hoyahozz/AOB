package com.dongyang.android.boda.Riding.Map.Marker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.dongyang.android.boda.Riding.Map.Model.Bike.BikeItem;
import com.dongyang.android.boda.R;
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


