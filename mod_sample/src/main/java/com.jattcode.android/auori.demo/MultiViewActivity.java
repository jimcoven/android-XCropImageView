/*
 *
 *  * Copyright (c) 2015. JattCode.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 *
 */

package com.jattcode.android.auori.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.jattcode.android.auori.view.AuoriCropImageView;

import java.util.ArrayList;
import java.util.List;

public class MultiViewActivity extends AppCompatActivity {

    private static class Configuration {
        final int scaleCropType;
        final int alignment;

        private Configuration(int type, int align) {
            scaleCropType = type;
            alignment = align;
        }

        private String getConfiguration() {
            return getCropType(scaleCropType) + " : " + getAlignment(alignment);
        }

        private static String getCropType(int id) {
            switch(id) {
                case 0: return "FIT_WIDTH";
                case 1: return "FIT_FILL";
                case 2: return "FIT_HEIGHT";
            }
            return "NA";
        }

        private static String getAlignment(int id) {
            switch(id) {
                case 1: return "ALIGN_TOP_OF_IMAGEVIEW";
                case 2: return "ALIGN_BOTTOM_OF_IMAGEVIEW";
                case 3: return "ALIGN_CENTER_OF_IMAGEVIEW";
                case 4: return "ALIGN_LEFT_OF_IMAGEVIEW";
                case 5: return "ALIGN_RIGHT_OF_IMAGEVIEW";
            }
            return "NA";
        }
    }

    private static final List<Configuration[]> configs;

    static {
        configs = new ArrayList<>();

        for (int type = 0; type < 3; type++) {
            Configuration[] set = new Configuration[5];
            for (int k = 0; k < 5; k++) {
                set[k] = new Configuration(type, k+1);
            }
            configs.add(set);
        }

//        configs.get(0)[3] = null; // FIT_WIDTH + ALIGN_LEFT
//        configs.get(0)[4] = null; // FIT_WIDTH + ALIGN_RIGHT
//
//        configs.get(2)[0] = null; // FIT_HEIGHT + ALIGN_TOP
//        configs.get(2)[1] = null; // FIT_HEIGHT + ALIGN_BOTTOM
    }

    private static LinearLayout createLayout(Context context, LayoutParams lp, int orient) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(lp);
        layout.setOrientation(orient);
        layout.setBackgroundColor(Color.CYAN);
        return layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_multiview);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.setOrientation(LinearLayout.VERTICAL);
//        container.setWeightSum(configs.size());

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        List<ImageView> views = new ArrayList<>();

        for (int c = 0; c < configs.size(); c++) {

            LinearLayout ll = createLayout(this, lp, LinearLayout.HORIZONTAL);
            Configuration[] set = configs.get(c);
//            ll.setWeightSum(set.length);

            for (int k = 0; k < set.length; k++) {
                AuoriCropImageView auori = new AuoriCropImageView(this);
                if (set[k] != null) {
                    auori.setCropType(set[k].scaleCropType);
                    auori.setCropAlignment(set[k].alignment);
                    views.add(auori);
                }
                auori.setLayoutParams(lp);
                ll.addView(auori);
            }
            container.addView(ll);
        }

        container.forceLayout();

        for(ImageView view : views) {
            view.setImageResource(R.mipmap.long_1);
        }

//
//        final ImagePageAdapter adapter = new ImagePageAdapter(this, configs);
//        final ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
//        pager.setAdapter(adapter);
//        title.setText(pager.getAdapter().getPageTitle(0));
//
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                title.setText(adapter.getPageTitle(position));
//                View view = adapter.getItem(position);
//                if (view != null) {
//                    ((ImagePageAdapter.VHItem)view.getTag()).sync();
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }


    private static class ImagePageAdapter extends ObjectPageAdaper {

        private class VHItem {
            AuoriCropImageView image;
            int resId;

            VHItem(View itemView) {
                this.image = (AuoriCropImageView) itemView;
                itemView.setOnClickListener(clicker);
                itemView.setTag(this);
            }

            private void sync() {
                resId = DataPack.with(context).currentId();;
                image.setImageResource(resId);
            }

            private void next() {
                resId = DataPack.with(context).nextId();
                image.setImageResource(resId);
            }
        }

        private final View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VHItem) view.getTag()).next();
            }
        };

        private final LayoutInflater inflater;

        private final Context context;

        private final Configuration[] configs;

        ImagePageAdapter(Context context, Configuration[] configs) {
            this.context = context;
            this.configs = configs;
            this.inflater = LayoutInflater.from(context);
        }

        public String getPageTitle(int position) {
            return configs[position].getConfiguration();
        }

        public View getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getCount() {
            return configs.length;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            VHItem holder;
            if (view == null) {
                view = inflater.inflate(R.layout.auori_cropimageview, parent, false);
                holder = new VHItem(view);
            } else {
                holder = (VHItem) view.getTag();
            }

            bindView(holder, position);

            return view;
        }

        private void bindView(final VHItem holder, int position) {
            Configuration config = configs[position];
            holder.resId = DataPack.with(context).currentId();
            holder.image.setCropType(config.scaleCropType);
            holder.image.setCropAlignment(config.alignment);
            holder.image.setImageResource(holder.resId);
            holder.image.forceLayout();
        }

    }

    /**
     * A page adapter which works with a large data set by reusing views.
     */
    private abstract static class ObjectPageAdaper extends PagerAdapter {

        private static class RecycleItem {
            private int position = -1;
            private View view;
        }

        // Views that can be reused.
        private final List<RecycleItem> recycler = new ArrayList<>();
        private final List<RecycleItem> active = new ArrayList<>();

        @Override
        public abstract int getCount();

        public abstract View getView(int position, View view, ViewGroup parent);

        @Override
        public Object instantiateItem(ViewGroup parent, int position) {
            RecycleItem item = recycler.isEmpty() ? new RecycleItem() : recycler.remove(0);
            item.view = getView(position, item.view, parent);
            item.position = position;

            parent.addView(item.view);
            active.add(item);

            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            RecycleItem item = (RecycleItem) object;
            if (item != null) {
                item.position = -1;
                recycler.add(item);
                container.removeView(item.view);
                active.remove(item);
            }
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == ((RecycleItem) obj).view;
        }

        /**
         * Attempts to return the view based on position.
         * It can only return -x/+x of current position of the view pager.
         * @param position
         * @return view or null. If it returns null, it means its being recycled at the moment
         */
        public View getItem(int position) {
            for (RecycleItem item : active) {
                if (item.position == position) {
                    return item.view;
                }
            }
            return null;
        }

        @Override
        public void notifyDataSetChanged() {
            recycler.clear();
            super.notifyDataSetChanged();
        }
    }

}