package com.hb.jensenhaw.rfidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.UHF.UhfManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;

public class UHFActivity extends AppCompatActivity implements OnClickListener
		 {
/****************** for view:**************************************/
	private LinearLayout l1;
	private LinearLayout l2;
	private LinearLayout l3;
	private LinearLayout l4;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private View view1;
	private View view2;
	private View view3;
	private View view4;
	private RelativeLayout l1epc;
	private LinearLayout l2readandwrite;
	private LinearLayout l3lockandkill;
	private LinearLayout l4settings;
	private LinearLayout l5moresettings;
	private Button button_moresetting;
		/******************************************/
	/****for uhf operation:**************************************/
		/******************************************/
	private Spinner spinnerEPCRead;
	private Spinner spinnerEPCLock;
	private Button buttonClear;
	private Button buttonStart;
	private TextView textVersion;
	private ListView listViewData;
	private ArrayList<EPC> listEPC;
	private ArrayList<String> listepc = new ArrayList<String>();
	private ArrayAdapter<String> arr_adapter;
	private ArrayList<Map<String, Object>> listMap;
	private boolean runFlag = true;
	private boolean startFlag = false;
	private UhfManager manager; // UHF manager,UHF Operating handle
//	private ScreenStateReceiver screenReceiver;
	/******************************************/
	private Spinner spinnerMemBank;// mem area
	private EditText editPassword;// password
	private EditText editAddr;// begin address
	private EditText editLength;// read data length
	private Button buttonRead;
	private Button buttonWrite;
	private EditText editWriteData;// write data
	private EditText editReadData;// read data
	// RESERVE EPC TID USER:0,1,2,3
	private final String[] strMemBank = { "RESERVE", "EPC", "TID", "USER" };
	/************************************/
	private ArrayAdapter<String> adatpterMemBank;
	private Spinner spinnerLockType;//
	private Button buttonLock;//
	private EditText editKillPassword;//
	private Button buttonKill;//
	private ArrayAdapter<CharSequence> adapterLockType;
	private int membank;// 
	private int lockMembank;
	private int addr = 0;// begin address 
	private int length = 1;// read or write  data length
	private int lockType;//
	private Button buttonBack;
	/******************************************/
	private Button button1;//set button1
	private Button button2;//set button2
	private Button button3;//set button3
	private Button button4;//set button4
	private Spinner spinnerSensitive;//The sensitivity
	private Spinner spinnerPower;//RF power
	private Spinner spinnerWorkArea;//work area
	private EditText editFrequency;// frequency
	private String[] powers = {"26dbm","24dbm","20dbm","18dbm","17dbm"};
	private String[] sensitives = null;
	
	private String[] areas = null;
	private ArrayAdapter<String> adapterSensitive;
	private ArrayAdapter<String> adapterPower;
	private ArrayAdapter<String> adapterArea;
	private int sensitive = 0;
	private int power = 0 ;//rate of work
	private int area = 0;
	private int frequency = 0;
	
	private String what = "uhf";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setOverflowShowingAlways();
		setContentView(R.layout.activity_uhf);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if(actionBar != null){
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		// Get the Rf power, and set
		shared = getSharedPreferences("UhfRfPower", 0);
		editor = shared.edit();
		power = shared.getInt("power", 26);
		area = shared.getInt("area", UhfManager.WorkArea_USA);
		//init view 
		initView();
		//start inventory thread
		Thread thread = new InventoryThread();
		thread.start();
		// init sound pool
		Util.initSoundPool(this);
	}

	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String powerString = "";
		switch (UhfManager.Power) {
		case SerialPort.Power_3v3:
			powerString = "power_3V3";
			break;
		case SerialPort.Power_5v:
			powerString = "power_5V";
			break;
		case SerialPort.Power_Scaner:
			powerString = "scan_power";
			break;
		case SerialPort.Power_Psam:
			powerString = "psam_power";
			break;
		case SerialPort.Power_Rfid:
			powerString = "rfid_power";
			break;
		default:
			break;
		}
		TextView textView_title_config;
		textView_title_config = (TextView) findViewById(R.id.textview_title_config);
		textView_title_config.setText("Port:com" + UhfManager.Port
				+";Power:" + powerString);
		manager = UhfManager.getInstance();
		if (manager == null) {
			textVersion.setText(getString(R.string.serialport_init_fail_));
			setButtonClickable(buttonClear, false);
			setButtonClickable(buttonStart, false);
			return;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		Log.e("", "value" + power);
		manager.setOutputPower(power);
		manager.setWorkArea(area);
		byte[] version_bs = manager.getFirmware();
		if (version_bs!=null){
			textView_title_config.append("("+new String(version_bs)+")");
		}
	}
	@Override
	protected void onPause() {
		startFlag = false;
		buttonStart.setText(R.string.inventory);
		manager.close();
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		startFlag = false;
		runFlag = false;
		if (manager != null) {
			manager.close();
		}
		super.onDestroy();
	}

	private void initView() {
		buttonStart = (Button) findViewById(R.id.button_start);
		buttonClear = (Button) findViewById(R.id.button_clear);
		listViewData = (ListView) findViewById(R.id.listView_data);
		textVersion = (TextView) findViewById(R.id.textView_version);
		buttonStart.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		listEPC = new ArrayList<EPC>();
		l1 = (LinearLayout) findViewById(R.id.linearLayoutUhfEpc);
		l1.setOnTouchListener(new myOnTouch());
//		l2 = (LinearLayout) findViewById(R.id.linearLayoutUhfRead);
//		l2.setOnTouchListener(new myOnTouch());
//		l3 = (LinearLayout) findViewById(R.id.linearLayoutUhfLock);
//		l3.setOnTouchListener(new myOnTouch());
		l4 = (LinearLayout) findViewById(R.id.linearLayoutUhfSet);
		l4.setOnTouchListener(new myOnTouch());
		textView1 = (TextView) findViewById(R.id.textViewUhfEpc);
//		textView2 = (TextView) findViewById(R.id.textViewUhfMore);
//		textView3 = (TextView) findViewById(R.id.textViewUhfLock);
		textView4 = (TextView) findViewById(R.id.textViewUhfSet);
		view1 = findViewById(R.id.viewUhfEpc);
//		view2 = findViewById(R.id.viewUhfMore);
//		view3 = findViewById(R.id.viewUhfLock);
		view4 = findViewById(R.id.viewUhfSet);
		l1epc = (RelativeLayout) findViewById(R.id.l1epc);
		l2readandwrite = (LinearLayout) findViewById(R.id.l2read);
		l3lockandkill = (LinearLayout) findViewById(R.id.l3lock);
		l4settings = (LinearLayout) findViewById(R.id.l4settings);

		arr_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listepc);
		arr_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerEPCRead = (Spinner) findViewById(R.id.spinnerEPCread);
		spinnerEPCRead.setAdapter(arr_adapter);
		spinnerEPCLock = (Spinner) findViewById(R.id.spinnerEPClock);
		spinnerEPCLock.setAdapter(arr_adapter);
		spinnerMemBank = (Spinner) findViewById(R.id.spinner_membank);
		editAddr = (EditText) findViewById(R.id.edittext_addr);
		editLength = (EditText) findViewById(R.id.edittext_length);
		editPassword = (EditText) findViewById(R.id.editTextPassword);
		buttonRead = (Button) findViewById(R.id.button_read);
		buttonWrite = (Button) findViewById(R.id.button_write);
		buttonClear = (Button) findViewById(R.id.button_readClear);
		buttonLock = (Button) findViewById(R.id.button_lock_6c);
		buttonKill = (Button) findViewById(R.id.button_kill_6c);
		buttonBack = (Button) findViewById(R.id.button_back);
		button_moresetting = (Button) findViewById(R.id.button_uhf_more_settings);
		l5moresettings = (LinearLayout) findViewById(R.id.layout_uhf_more_settings);
		button_moresetting.setOnClickListener(this);
		editKillPassword = (EditText) findViewById(R.id.edit_kill_password);
		editWriteData = (EditText) findViewById(R.id.edittext_write);
		editReadData = (EditText) findViewById(R.id.linearLayout_readData);
		adatpterMemBank = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, strMemBank);
		adatpterMemBank
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerLockType = (Spinner) findViewById(R.id.spinner_lock_type);
		adapterLockType = ArrayAdapter.createFromResource(this,
				R.array.arr_lockType, android.R.layout.simple_spinner_item);
		adapterLockType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerLockType.setAdapter(adapterLockType);
		spinnerMemBank.setAdapter(adatpterMemBank);
		buttonClear.setOnClickListener(this);
		buttonRead.setOnClickListener(this);
		buttonWrite.setOnClickListener(this);
		buttonKill.setOnClickListener(this);
		buttonLock.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
		spinnerMemBank.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				switch (arg2) {
				case 0:
					membank = UhfManager.RESERVE;
					break;
				case 1:
					membank = UhfManager.EPC;
					break;
				case 2:
					membank = UhfManager.TID;
					break;
				case 3:
					membank = UhfManager.USER;
					break;
				default:
					break;
				}
				
				lockMembank = arg2 + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		spinnerLockType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				lockType = arg2;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		spinnerEPCRead.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
//				manager.selectEPC(Tools.HexString2Bytes(listepc.get(position)));
				spinnerEPCRead.setSelection(position,true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		spinnerEPCLock.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
//				manager.selectEPC(Tools.HexString2Bytes(listepc.get(position)));
				spinnerEPCLock.setSelection(position,true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		button1 = (Button) findViewById(R.id.button_min);
		button2 = (Button) findViewById(R.id.button_plus);
		button3 = (Button) findViewById(R.id.button_set);
		button4 = (Button) findViewById(R.id.button4);
		
		spinnerSensitive = (Spinner) findViewById(R.id.spinner1);
		spinnerPower = (Spinner) findViewById(R.id.spinner2);
		spinnerWorkArea = (Spinner) findViewById(R.id.spinner3);
		editFrequency = (EditText) findViewById(R.id.edit4);
		sensitives = getResources().getStringArray(R.array.arr_sensitivity);
		areas = getResources().getStringArray(R.array.arr_area);
		
		adapterSensitive = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sensitives);
		adapterPower = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, powers);
		adapterArea = new ArrayAdapter<String>(this	, android.R.layout.simple_dropdown_item_1line, areas);
		spinnerSensitive.setAdapter(adapterSensitive);
		spinnerPower.setAdapter(adapterPower);
		spinnerWorkArea.setAdapter(adapterArea);
		int power_position = 0;
		switch (power) {
		case 26:
			power_position = 0;
			break;
		case 24:
			power_position = 1;
			break;
		case 20:
			power_position = 2;
			break;
		case 18:
			power_position = 3;
			break;
		case 17:
			power_position = 4;
			break;
		default:
			break;
		}
		spinnerPower.setSelection(power_position, true);
		int area_position = area-1;
		spinnerWorkArea.setSelection(area_position, true);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		spinnerWorkArea.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
									   int position, long id) {
				switch (position) {
				case 0:
					area = 1;
					break;
				case 1:
					area = 2;
					break;
				case 2:
					area = 3;
					break;
				case 3:
					area = 4;
					break;
				case 4:
					area = 6;
					break;
				default:
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spinnerSensitive.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
				
				Log.e("", sensitives[position]);
				switch (position) {
				case 0:
					sensitive = UhfManager.SENSITIVE_HIHG;
					break;
				case 1:
					sensitive = UhfManager.SENSITIVE_MIDDLE;
					break;
				case 2:
					sensitive = UhfManager.SENSITIVE_LOW;
					break;
				case 3:
					sensitive = UhfManager.SENSITIVE_VERY_LOW;
					break;

				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spinnerPower.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
				Log.e("", powers[position]);
				switch (position) {
				case 0:
					power = 26;
					break;
				case 1:
					power =24;
					break;
				case 2:
					power = 20;
					break;
				case 3:
					power = 18;
					break;
				case 4:
					power = 17;
					break;
				default:
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	

	/**
	 * Inventory EPC Thread
	 */
	class InventoryThread extends Thread {
		private List<byte[]> epcList;

		@Override
		public void run() {
			super.run();
			while (runFlag) {
				if (startFlag) {
					// manager.stopInventoryMulti()
					epcList = manager.inventoryRealTime(); // inventory real time
					if (epcList != null && !epcList.isEmpty()) {
						// play sound 
						Util.play(1, 0);
						for (byte[] epc : epcList) {
							String epcStr = Tools.Bytes2HexString(epc,
									epc.length);
							addToList(listEPC, epcStr);
						}
					}
					epcList = null;
					try {
						Thread.sleep( 40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	// EPC add to LISTVIEW
	private void addToList(final List<EPC> list, final String epc) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// The epc for the first time
				if (list.isEmpty()) {
					EPC epcTag = new EPC();
					epcTag.setEpc(epc);
					epcTag.setCount(1);
					list.add(epcTag);
					listepc.add(epc);
				} else {
					for (int i = 0; i < list.size(); i++) {
						EPC mEPC = list.get(i);
						// list contain this epc 
						if (epc.equals(mEPC.getEpc())) {
							mEPC.setCount(mEPC.getCount() + 1);
							list.set(i, mEPC);
							break;
						} else if (i == (list.size() - 1)) {
							// list doesn't contain this epc
							EPC newEPC = new EPC();
							newEPC.setEpc(epc);
							newEPC.setCount(1);
							list.add(newEPC);
							listepc.add(epc);
						}
					}
				}
				// add the data to ListView
				listMap = new ArrayList<Map<String, Object>>();
				int idcount = 1;
				for (EPC epcdata : list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("ID", idcount);
					map.put("EPC", epcdata.getEpc());
					map.put("COUNT", epcdata.getCount());
					idcount++;
					listMap.add(map);
				}
				listViewData.setAdapter(new SimpleAdapter(UHFActivity.this,
						listMap, R.layout.listview_item, new String[] { "ID",
								"EPC", "COUNT" }, new int[] {
								R.id.textView_list_item_id,
								R.id.textView_list_item_barcode,
								R.id.textView_list_item_count }));
				spinnerEPCRead.setAdapter(arr_adapter);
				spinnerEPCLock.setAdapter(arr_adapter);
				listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Bundle bundle = new Bundle();
						bundle.putString("message", listMap.get(arg2).get("EPC").toString());
						Intent intent = new Intent();
						intent.putExtras(bundle);
						intent.setClass(UHFActivity.this, RFIDDetailActivity.class);
						Log.e("message", listMap.get(arg2).get("EPC").toString());
						startActivity(intent);
					}
				});
			}
		});
	}

	// Make the button clickable or unclickable
	private void setButtonClickable(Button button, boolean flag) {
		button.setClickable(flag);
		if (flag) {
			button.setTextColor(Color.BLACK);
		} else {
			button.setTextColor(Color.GRAY);
		}
	}

	
	/**
	 * clear list and listview O
	 */
	private void clearData() {
		listEPC.removeAll(listEPC);
		listViewData.setAdapter(null);
		listepc.removeAll(listepc);
	}

	@Override
	public void onClick(View v) {
		byte[] accessPassword = Tools.HexString2Bytes(editPassword.getText()
				.toString());
		addr = Integer.valueOf(editAddr.getText().toString());
		length = Integer.valueOf(editLength.getText().toString());
		switch (v.getId()) {
		case R.id.button_start:
			if (!startFlag) {
				startFlag = true;
				buttonStart.setText(R.string.stop_inventory);
			} else {
				startFlag = false;
				buttonStart.setText(R.string.inventory);
			}
			break;
		case R.id.button_clear:
			clearData();
			break;
		case R.id.button_read:
			if (accessPassword.length != 4) {
				Toast.makeText(getApplicationContext(), R.string.password_is_4_bytes,
						Toast.LENGTH_SHORT).show();
				return;
			}
			// read data 
			byte[] data = manager.readFrom6C(membank, addr, length,
					accessPassword);
			if (data != null && data.length > 1) {
				String dataStr = Tools.Bytes2HexString(data, data.length);
				editReadData.append(getString(R.string.read_data_) + dataStr + "\n");
			} else {
				if (data != null) {
					editReadData.append(getString(R.string.read_fail_error) + (data[0] & 0xff) + "\n");
					return;
				}
				editReadData.append(getString(R.string.read_fail_return_null) + "\n");
			}
			break;
		// Write data to tag 
		case R.id.button_write:
			if (accessPassword.length != 4) {
				Toast.makeText(getApplicationContext(), R.string.password_is_4_bytes,
						Toast.LENGTH_SHORT).show();
				return;
			}
			String writeData = editWriteData.getText().toString();
			if (writeData.length() % 4 != 0) {
				Toast.makeText(getApplicationContext(),
						R.string.the_unit_is_word_1word_2bytes, Toast.LENGTH_SHORT)
						.show();
			}
			byte[] dataBytes = Tools.HexString2Bytes(writeData);
			// dataLen = dataBytes/2 dataLen
			boolean writeFlag = manager.writeTo6C(accessPassword, membank,
					addr, dataBytes.length / 2, dataBytes);
			if (writeFlag) {
				editReadData.append(getString(R.string.write_successful_) + "\n");
			} else {
				editReadData.append(getString(R.string.write_failue_) + "\n");
			}
			break;
		// lock tag
//		case R.id.button_lock_6c:
//			String password = editPassword.getText().toString();
//			if (manager.lock6C(Tools.HexString2Bytes(password), membank, lockType)) {
//				Toast.makeText(UHFActivity.this, R.string.lock_successful_, 0).show();
//			}else {
//	 			Toast.makeText(UHFActivity.this, R.string.lock_fail_, 0).show();
//			}
//			break;
//		// kill tag
//		case R.id.button_kill_6c:
//			break;
//		// clear read data
//		case R.id.button_readClear:
//			editReadData.setText("");
//			break;
//		case R.id.button_min:
//			manager.setSensitivity(sensitive);
//			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
//			break;
		case R.id.button_plus:
			editor.putInt("power", power);
			editor.commit();
			if (manager.setOutputPower(power)) {
				Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			}
			break;
		case R.id.button_set:
			manager.setWorkArea(area);
			editor.putInt("area", area);
			editor.commit();
			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			break;
		case R.id.button4:
			String freqStr = editFrequency.getText().toString();
			if(freqStr == null || "".equals(freqStr)){
				Toast.makeText(getApplicationContext(), R.string.freqSetting, 0).show();
				return;
			}
//			manager.setFrequency(frequency, 0, 0);
			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			break;
		case R.id.button_uhf_more_settings:
			l5moresettings.setVisibility(View.GONE);
			AlertDialog dlg = new AlertDialog.Builder(UHFActivity.this)
	        .setTitle(R.string.note_the_following_operation_may_lead_to_module_does_not_work_)
	        
	        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	l5moresettings.setVisibility(View.VISIBLE);
	          }
	        })
	        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        })
	        .create();
	        dlg.show();
			
			break;
		default:
			break;
		}
	}
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
			 case android.R.id.home:
				 this.finish(); // back button
				 return true;
		 }
		 return super.onOptionsItemSelected(item);
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * on actionbar show menu button
	 */
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class myOnTouch implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent arg1) {
			switch (view.getId()) {
			case R.id.linearLayoutUhfEpc:
				SetVisible(null, textView1, view1);
				break;
//			case R.id.linearLayoutUhfRead:
//				if (listEPC.size()>0)
//				SetVisible(l2readandwrite, textView2, view2);
//				break;
//			case R.id.linearLayoutUhfLock:
//				if (listEPC.size()>0)
//				SetVisible(l3lockandkill, textView3, view3);
//				break;
			case R.id.linearLayoutUhfSet:
				SetVisible(l4settings, textView4, view4);
				break;
			default:
				break;
			}
			return true;
		}
	}

	private void SetVisible(LinearLayout layout, TextView textView, View view) {
		l1epc.setVisibility(View.GONE);
		l2readandwrite.setVisibility(View.GONE);
		l3lockandkill.setVisibility(View.GONE);
		l4settings.setVisibility(View.GONE);

		textView1.setTextColor(getResources().getColor(R.color.black));
		view1.setBackgroundColor(getResources().getColor(R.color.white));
//		textView2.setTextColor(getResources().getColor(R.color.black));
//		view2.setBackgroundColor(getResources().getColor(R.color.white));
//		textView3.setTextColor(getResources().getColor(R.color.black));
//		view3.setBackgroundColor(getResources().getColor(R.color.white));
		textView4.setTextColor(getResources().getColor(R.color.black));
		view4.setBackgroundColor(getResources().getColor(R.color.white));
		textView.setTextColor(getResources().getColor(R.color.tabSelect));
		view.setBackgroundColor(getResources().getColor(R.color.tabSelect));
		if (layout==null) {
			l1epc.setVisibility(View.VISIBLE);
			startFlag = false;
			buttonStart.setText(R.string.inventory);
		}else {
			layout.setVisibility(View.VISIBLE);
		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (System.currentTimeMillis() - exitTime < 2000) {
				finish();
//			} else {
//				exitTime = System.currentTimeMillis();
//				Toast.makeText(this, "Double click to exit!", 0).show();
//				return true;
//			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
