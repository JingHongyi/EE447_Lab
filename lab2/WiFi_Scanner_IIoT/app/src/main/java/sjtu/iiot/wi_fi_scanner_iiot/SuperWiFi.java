package sjtu.iiot.wi_fi_scanner_iiot;

/*****************************************************************************************************************
 * Created by HelloShine on 2019-3-24.
 * ***************************************************************************************************************/
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log; //Log can be utilized for debug.

public class SuperWiFi extends MainActivity{

    /*****************************************************************************************************************
     * When you run the APP in your mobile phone, you can utilize the following code for debug:
     * Log.d("TEST_INFO","Your Own String Type Content Here");
     * You can also generate the String via ("String" + int/double value). for example, "CurTime " + 20 = "CurTime 20"
     * ***************************************************************************************************************/
    private String FileLabelName = "WifiScanner";// Define the file Name
    /*****************************************************************************************************************
     * You can define the Wi-Fi SSID to be measured in FileNameGroup, more than 2 SSIDs are OK.
     * It is noting that multiple Wi-Fi APs might share the same SSID such as SJTU.
     * ***************************************************************************************************************/
    private String FileNameGroup[] = {"AndroidWifi"};

    private int TestTime = 10;//Number of measurement
    int ScanningTime = 1000;//Wait for (?) ms for next scan

    private int NumberOfWiFi = FileNameGroup.length;

    // RSS_Value_Record and RSS_Measurement_Number_Record are used to record RSSI values
    private int[] RSS_Value_Record = new int[NumberOfWiFi];
    private int[] RSS_Measurement_Number_Record = new int[NumberOfWiFi];
    private int[] RSS_Frequency_Record = new int[NumberOfWiFi];
    private String[] RSS_Capability_Record = new String[NumberOfWiFi];
    private double[] CurrentLocation = new double[2];
    private int IPaddress;


    private WifiManager mWiFiManager = null;
    private LocationManager mlocationManager = null;
    private Vector<String> scanned = null;
    boolean isScanning = false;
    int x_position;
    int y_position;
    String provider;
    WifiInfo wifiInfo = null;

    public SuperWiFi(Context context)
    {
        this.mWiFiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.mlocationManager = (LocationManager)context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        this.scanned = new Vector<String>();
    }

    private void startScan()//The start of scanning
    {
        this.isScanning = true;
        Thread scanThread = new Thread(new Runnable()
        {
            public void run() {
                scanned.clear();//Clear last result
                for(int index = 1;index <= NumberOfWiFi; index++){
                    RSS_Value_Record[index - 1] = 0;
                    RSS_Measurement_Number_Record[index - 1] = 1;
                    RSS_Frequency_Record[index - 1] = 0;
                }
                int CurTestTime = 1; //Record the test time and write into the SD card
                SimpleDateFormat formatter = new SimpleDateFormat
                        ("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis()); //Get the current time
                String CurTimeString = formatter.format(curDate);
                for(int index = 1;index <= NumberOfWiFi; index++){
                    write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt","Test_ID: " + testID + " TestTime: " + CurTimeString + " BEGIN\r\n");
                }
                //Scan for a certain times
                while(CurTestTime++ <= TestTime) performScan();
                WifiLocalization();
                for(int index = 1;index <= NumberOfWiFi; index++){//Record the average of the result
                    scanned.add("Name="+FileLabelName + "-" + FileNameGroup[index - 1]+"\nIP="+IPaddress+"\nCapability="+RSS_Capability_Record[index - 1]+"\nSignal="+ RSS_Value_Record[index - 1]/ RSS_Measurement_Number_Record[index - 1] +"\nFrequency="+RSS_Frequency_Record[index-1]+"\nLocation=x:"+x_position+" y:"+y_position+"\r\n");

                }

                /*****************************************************************************************************************



                 * ***************************************************************************************************************/
                for(int index = 1;index <= NumberOfWiFi; index++){//Mark the end of the test in the file
                    write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt","testID:"+testID+"END\r\n");
                }
                isScanning=false;
            }
        });
        scanThread.start();
    }
    //realize the localization of Wifi
    private void WifiLocalization(){
        if(mlocationManager == null)
            return;
        //get all the location provider
        List<String> providerList = mlocationManager.getProviders(true);
        //to set the localizer to Wifi localizer
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            this.provider = LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            this.provider = LocationManager.NETWORK_PROVIDER;
        } else {
            return;
        }
        //get the newst location of Wifi
        Location location = mlocationManager.getLastKnownLocation(provider);
        //get the altitude and longtitude
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        CurrentLocation[0] = latitude;
        CurrentLocation[1] = longitude;

    }
    private void FingerPrint(){
        int[] r_vector = new int[NumberOfWiFi];
        for(int i=1;i<=NumberOfWiFi;i++){
            r_vector[i-1] = RSS_Value_Record[i - 1]/ RSS_Measurement_Number_Record[i - 1];
        }
        int x_grid = 5;
        int y_grid = 5;
        int distance = 10000;
        for(int wifi=1;wifi<=NumberOfWiFi;wifi++) {
            for (int i = 1; i <= x_grid; i++) {
                for (int j = 1; j < y_grid; j++) {

                    if ((i - r_vector[wifi-1])*(i - r_vector[wifi-1])+ (j - r_vector[wifi-1])*(j - r_vector[wifi-1])<distance){
                        distance = (i - r_vector[wifi-1])*(i - r_vector[wifi-1])+ (j - r_vector[wifi-1])*(j - r_vector[wifi-1]);
                        x_position = i;
                        y_position = j;
                    }
                }
            }
        }
    }
    private void performScan()//The realization of the test
    {
        if(mWiFiManager == null)
            return;
        try
        {
            if(!mWiFiManager.isWifiEnabled())
            {
                mWiFiManager.setWifiEnabled(true);
            }
            mWiFiManager.startScan();//Start to scan
            try {
                Thread.sleep(ScanningTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.scanned.clear();
            List<ScanResult> sr = mWiFiManager.getScanResults();
            Iterator<ScanResult> it = sr.iterator();
            wifiInfo = mWiFiManager.getConnectionInfo();
            IPaddress = wifiInfo.getIpAddress();
            while(it.hasNext())
            {
                ScanResult ap = it.next();
                for(int index = 1;index <= FileNameGroup.length; index++){
                    if (ap.SSID.equals(FileNameGroup[index - 1])){//Write the result to the file
                        RSS_Value_Record[index-1] = RSS_Value_Record[index-1] + ap.level;
                        RSS_Measurement_Number_Record[index - 1]++;
                        RSS_Frequency_Record[index - 1] = ap.frequency;
                        RSS_Capability_Record[index - 1] = ap.capabilities;
                        write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt",ap.level+"\r\n");
                    }
                }
            }
        }
        catch (Exception e)
        {
            this.isScanning = false;
            this.scanned.clear();
        }
    }




    public void ScanRss(){
        startScan();
    }
    public boolean isscan(){
        return isScanning;
    }
    public Vector<String> getRSSlist(){
        return scanned;
    }

    private void write2file(String filename, String a){//Write to the SD card
        try {
            File file = new File("/sdcard/"+filename);
            if (!file.exists()){
                file.createNewFile();} // Open a random filestream by Read&Write
            RandomAccessFile randomFile = new
                    RandomAccessFile("/sdcard/"+filename, "rw"); // The length of the file(byte)
            long fileLength = randomFile.length(); // Put the writebyte to the end of the file
            randomFile.seek(fileLength);
            randomFile.writeBytes(a);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}