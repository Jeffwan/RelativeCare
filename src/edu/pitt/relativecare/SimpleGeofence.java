/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.pitt.relativecare;

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center (latitude and longitude position) and radius.
 */
public class SimpleGeofence {
    // Instance variables
	// 这里需要注意，id 是String类型, Transition是 int类型
    private final String mId;
    private final String mName;
    private final String mAddress;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;

    /**
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     * validity. default is 12 hour.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    
    // 构造函数
    public SimpleGeofence(
            String geofenceId,
            String geofencename,
            String geoAddress,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
        // Set the instance fields from the constructor

        // An identifier for the geofence
        this.mId = geofenceId;
        
        // Center of the geofence
        this.mName = geofencename;
        this.mAddress = geoAddress;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;  // in meters
        this.mExpirationDuration = expiration; // in milliseconds
        this.mTransitionType = transition;
    }
    // Instance field getters

    public String getId() {
        return mId;
    }

    public String getName() {
		return mName;
	}
    
    public String getAddress() {
    	return mAddress;
    }
    
    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getRadius() {
        return mRadius;
    }

    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a	
     * SimpleGeofence.
     * 
     * 这个主要不是为了创建，create 还是通过构造函数，这个主要是用于取出一个Object的, 我还是不懂这是干什么的
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                       .setRequestId(getId())
                       .setTransitionTypes(mTransitionType)
                       .setCircularRegion(
                               getLatitude(),
                               getLongitude(),
                               getRadius())
                       .setExpirationDuration(mExpirationDuration)
                       .build();
    }
}
