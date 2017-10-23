package com.adasleader.jason.adasleader.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.adasleader.jason.adasleader.R;


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

        if (isPrefChanged(preference, newValue)) {
            updateConfig(preference, newValue);

            //ListPreference need update summary
            if (preference instanceof ListPreference)
                preference.setSummary((String)newValue);
            //update switch prefernece summary
            if (preference instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) preference;
                boolean myValue = (boolean) newValue;

                if (preference.getKey().equals(WarningConfig.stateTitle)){
                    if (sp.isChecked())
                        showStatementAlert(); //show alert dialog
                    preference.setSummary(myValue ? WarningConfig.stateSummaryOn : WarningConfig.stateSummaryOff);
                } else if (preference.getKey().equals(WarningConfig.autoReturnADAS)) {
                    preference.setSummary(myValue? WarningConfig.autoReturnADASOn: WarningConfig.autoReturnADASOff);
                } else if (preference.getKey().equals(WarningConfig.dvrVirtualBumper)) {
                    preference.setSummary(myValue? WarningConfig.dvrVirtualBumperOn: WarningConfig.dvrVirtualBumperOff);
                }
            }
        }
        return true;
    }

    private boolean isPrefChanged(Preference pref, Object newValue) {
        boolean result = false;
        if ((pref instanceof ListPreference) && (newValue instanceof String)) {
            //warning preferences
            ListPreference lp = (ListPreference) pref;
            String myValue = (String) newValue;
            if (!lp.getValue().equals(myValue)) {
                result = true;
            }
        } else if ((pref instanceof SwitchPreference) && (newValue instanceof Boolean)) {
            //statement preference
            SwitchPreference sp = (SwitchPreference) pref;
            boolean myValue = (boolean) newValue;
            if (sp.isChecked() != myValue) {
                result = true;
            }
        }
        return result;
    }

    private void showStatementAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_statement, null));

        builder.setPositiveButton(R.string.statement_ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initPreferences(PreferenceScreen root) {
        Context context = getActivity();

        //init warning preferences
        PreferenceCategory warningCategory = new PreferenceCategory(context);
        warningCategory.setTitle(WarningConfig.CATEGORY_TITLE_WARN);
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
            pref.setSummary(" ");
            pref.setOnPreferenceChangeListener(this);
            pref.setEnabled(false);
            warningCategory.addPreference(pref);
        }

        //init dvr preference
        PreferenceCategory dvrCategory = new PreferenceCategory(context);
        dvrCategory.setTitle(WarningConfig.CATEGORY_TITLE_DVR);
        root.addPreference(dvrCategory);

        //虚拟保险杠触发紧急视频
        SwitchPreference vbPref = new SwitchPreference(context);
        vbPref.setPersistent(false);
        vbPref.setKey(WarningConfig.dvrVirtualBumper);
        vbPref.setTitle(WarningConfig.dvrVirtualBumper);
        vbPref.setOnPreferenceChangeListener(this);
        vbPref.setEnabled(false);
        dvrCategory.addPreference(vbPref);

        //init Display preference
        PreferenceCategory displayCategory = new PreferenceCategory(context);
        displayCategory.setTitle(WarningConfig.CATEGORY_TITLE_DISPLAY);
        root.addPreference(displayCategory);

        //显示ADASLeader标志
        ListPreference logoPref = new ListPreference(context);
        logoPref.setPersistent(false);
        String[] values =WarningConfig.JS_DISPLAY_LOG_DESC;
        logoPref.setEntries(values);
        logoPref.setEntryValues(values);
        logoPref.setKey(WarningConfig.displayLogo);
        logoPref.setTitle(WarningConfig.displayLogo);
        logoPref.setDialogTitle(WarningConfig.displayLogo);
        logoPref.setSummary(" ");
        logoPref.setOnPreferenceChangeListener(this);
        logoPref.setEnabled(false);
        displayCategory.addPreference(logoPref);

        //特别提示
        SwitchPreference pref = new SwitchPreference(context);
        pref.setPersistent(false);
        pref.setKey(WarningConfig.stateTitle);
        pref.setTitle(WarningConfig.stateTitle);
        pref.setOnPreferenceChangeListener(this);
        pref.setEnabled(false);
        displayCategory.addPreference(pref);

        //自动回到ADASLeader界面
        SwitchPreference autoPref = new SwitchPreference(context);
        autoPref.setPersistent(false);
        autoPref.setKey(WarningConfig.autoReturnADAS);
        autoPref.setTitle(WarningConfig.autoReturnADAS);
        autoPref.setOnPreferenceChangeListener(this);
        autoPref.setEnabled(false);
        displayCategory.addPreference(autoPref);
    }

    //刷新报警设置
    //如果从汽车端读取到了报警配置文件，就更新
    //如果没有，则将所有的配置项清空
    public void refresh(WarningConfig config) {
        mConfig = config;
        boolean enabled = (mConfig != null);
        //refresh warning preferences
        for (int i = 0; i < WarningConfig.TITLES.length; i++) {
            Preference pref = findPreference(WarningConfig.TITLES[i]);
            if (null != pref && (pref instanceof ListPreference)) {
                pref.setEnabled(enabled);
                if (enabled) {
                    pref.setSummary("%s");
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
                    pref.setSummary(" ");
                    ((ListPreference) pref).setValue("");
                }
            }
        }

        Preference pref = findPreference(WarningConfig.dvrVirtualBumper);
        if (null != pref && (pref instanceof SwitchPreference)) {
            pref.setEnabled(enabled);
            if (enabled) {
                ((SwitchPreference) pref).setChecked(config.getDVRVirtualBumperSwitch());
                pref.setSummary(config.getDVRVirtualBumperSwitch() ?
                        WarningConfig.dvrVirtualBumperOn : WarningConfig.dvrVirtualBumperOff);
            } else {
                pref.setSummary(" ");
                ((SwitchPreference) pref).setChecked(false );
            }
        }

        pref = findPreference(WarningConfig.displayLogo);
        if (null!= pref && (pref instanceof ListPreference)){
            pref.setEnabled(enabled);
            if (enabled){
                pref.setSummary("%s");
                ((ListPreference) pref).setValue(config.displayLogoItem.getDesc());
            } else {
                //如果没有读取到配置文件，将各项值清空
                pref.setSummary(" ");
                ((ListPreference) pref).setValue("");
            }
        }

        //refresh statement preference
        pref = findPreference(WarningConfig.stateTitle);
        if (null != pref && (pref instanceof SwitchPreference)) {
            pref.setEnabled(enabled);
            if (enabled) {
                ((SwitchPreference) pref).setChecked(config.getStatementSwitch());
                pref.setSummary(config.getStatementSwitch() ? WarningConfig.stateSummaryOn :
                        WarningConfig.stateSummaryOff);
            } else {
                //如果没有读取到配置文件，将各项值清空
                pref.setSummary(" ");
                ((SwitchPreference) pref).setChecked(false);
            }
        }

        pref = findPreference(WarningConfig.autoReturnADAS);
        if (null != pref && (pref instanceof SwitchPreference)) {
            pref.setEnabled(enabled);
            if (enabled){
                boolean isChecked = config.getAutoReturnADASSwitch();
                ((SwitchPreference)pref).setChecked(isChecked);
                pref.setSummary(isChecked ? WarningConfig.autoReturnADASOn : WarningConfig.autoReturnADASOff);
            } else {
                //如果没有读取到配置文件，将各项值清空
                pref.setSummary(" ");
                ((SwitchPreference) pref).setChecked(false);
            }
        }

        updatePreferenceListHeight();
    }

    private void updateConfig(Preference preference, Object newValue) {
        if (null == mConfig || null == preference || null == newValue)
            return;

        boolean needUpdate = false;
        String itemTitle = preference.getKey();
        // warning preference
        WarningConfigItem item = mConfig.findItemByTitle(itemTitle);
        if (null != item) {
            item.setDesc((String)newValue);
            needUpdate = true;
        }

        // Statement preference
        if (itemTitle.equals(WarningConfig.stateTitle)) {
            mConfig.setStatementSwitch((boolean)newValue);
            needUpdate = true;
        }else if (itemTitle.equals(WarningConfig.autoReturnADAS)) {
            mConfig.setAutoReturnADASwitch((boolean)newValue);
            needUpdate = true;
        } else if (itemTitle.equals(WarningConfig.displayLogo)) {
            mConfig.displayLogoItem.setDesc((String)newValue);
            mConfig.setDisplayLogo();
            needUpdate = true;
        } else if (itemTitle.equals(WarningConfig.dvrVirtualBumper)) {
            mConfig.setDVRVirtualBumperSwitch((boolean) newValue);
            needUpdate = true;
        }

        if (needUpdate && null != mListener) {
            mListener.warningPreferencesChanged(this, mConfig);
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

    public void setListener(InteractionListener listener) {
        mListener = listener;
    }
}
