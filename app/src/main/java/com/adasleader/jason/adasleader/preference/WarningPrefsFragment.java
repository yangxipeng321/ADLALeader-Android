package com.adasleader.jason.adasleader.preference;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import hk.com.mobileye.jason.adasleader.R;


/**
 * Created by Jason on 2016/9/5.
 * Config warning preferences
 */

public class WarningPrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    public interface InteractionListener {
        void warningPreferencesChanged(WarningPrefsFragment fragment, WarningConfig config);
    }

    private WarningConfig mConfig= null;
    private InteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());
        setPreferenceScreen(root);
        initPreferences(root);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updatePreferenceListHeight();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //Toast.makeText(getActivity(), String.format("%s:%s", preference.getTitle(), newValue), Toast.LENGTH_SHORT).show();

        //如果是超速显示方式更改了，则需要更新其他超速设置项的使能状态
        if (preference.getKey().equals(WarningConfig.speedingDisplayStr)) {
            String value = (String)newValue;
            boolean isEnable = !value.equals(WarningConfig.JS_SPEEDING_DISPLAY_DESC[0]);
            for (int i = WarningConfig.SPEED_INDEX_MIN + 1; i <= WarningConfig.SPEED_INDEX_MAX; i++) {
                Preference pref = findPreference(WarningConfig.TITLES[i]);
                pref.setEnabled(isEnable);
            }
        }
        updateConfig(preference, newValue);

        return true;
    }

    private void initPreferences(PreferenceScreen root) {
        Context context = getActivity();
        PreferenceCategory warningCategory = new PreferenceCategory(context);
        warningCategory.setTitle(WarningConfig.TITLE);
        root.addPreference(warningCategory);

        for (int i = 0; i< WarningConfig.DESCS.length; i++) {
            ListPreference pref = new ListPreference(context);
            pref.setPersistent(false);
            String[] values = WarningConfig.DESCS[i];
            pref.setEntries(values);
            pref.setEntryValues(values);
            pref.setKey(WarningConfig.TITLES[i]);
            pref.setTitle(WarningConfig.TITLES[i]);
            pref.setDialogTitle(WarningConfig.TITLES[i]);
            pref.setSummary("%s");
            pref.setOnPreferenceChangeListener(this);
            pref.setEnabled(false);
            warningCategory.addPreference(pref);
        }
    }

    //刷新报警设置
    //如果从汽车端读取到了报警配置文件，就更新
    //如果没有，则将所有的配置项清空
    public void refresh(WarningConfig config) {
        mConfig = config;
        boolean enabled = (mConfig != null);
        for (int i = 0; i < WarningConfig.TITLES.length; i++) {
            Preference pref = findPreference(WarningConfig.TITLES[i]);
            if (null != pref && (pref instanceof ListPreference)) {
                pref.setEnabled(enabled);
                if (enabled) {
                    //根据读取的配置文件设置各项的值
                    ((ListPreference) pref).setValue(config.items.get(i).getDesc());

                    //根据超速显示方式决定是否使能其他的超速设置项
                    if (i > WarningConfig.SPEED_INDEX_MIN && i <= WarningConfig.SPEED_INDEX_MAX) {
                        if (mConfig.items.get(WarningConfig.SPEED_INDEX_MIN).getKey() == 0) {
                            pref.setEnabled(false);
                        }
                    }
                } else {
                    //如果没有读取到配置文件，将各项值清空
                    ((ListPreference) pref).setValue("");
                }
            }
        }
    }

    private void updateConfig(Preference preference, Object newValue) {
        if (null == mConfig || null == preference || null == newValue)
            return;

        String itemTitle = preference.getKey();
        WarningConfigItem item = mConfig.findItemByTitle(itemTitle);
        if (null != item) {
            item.setDesc((String)newValue);
//            Toast toast = Toast.makeText(getActivity(), String.format("%s: %s ---> %s",
//                    item.getTitle(), ((ListPreference) preference).getValue(), item.getDesc()),
//                    Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.TOP, 0, 20);
//            toast.show();
            if (null != mListener) {
                mListener.warningPreferencesChanged(this, mConfig);
            }
        }
    }

    private void updatePreferenceListHeight() {
        if (null != getView()) {
            ListView listView = (ListView) getView().findViewById(android.R.id.list);
            Adapter adapter = listView.getAdapter();

            if (null != adapter) {
//                int height = listView.getPaddingTop() + listView.getPaddingBottom();
                int height = 0;

                for (int i = 0; i < adapter.getCount(); i++) {
                    View item = adapter.getView(i, null, listView);
//                    if (item instanceof ViewGroup) {
//                        item.setLayoutParams(new ViewGroup.LayoutParams(
//                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    }
                    item.measure(0, 0);
                    height += item.getMeasuredHeight();
                }
                FrameLayout frame = (FrameLayout) getActivity().findViewById(R.id.warningPrefs);
                ViewGroup.LayoutParams params = frame.getLayoutParams();
                params.height = height + (listView.getDividerHeight() * adapter.getCount());
                frame.setLayoutParams(params);
            }
        }
    }
}
