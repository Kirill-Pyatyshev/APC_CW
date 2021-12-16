package apc;

import java.util.ArrayList;
import java.util.List;

public class Statistics {
    private final ProductionManager productionManager;
    private final SelectionManager selectionManager;
    private final Buffer buffer;

    private List<SourceResult> sourceResultList;
    private List<DeviceResult> deviceResultList;

    private List<SourceResult> sourcesCurrState;
    private List<DeviceResult> devicesCurrState;
    private List<BufferResult> bufferCurrState;

    private ArrayList<Double> dispWaitTime;
    private ArrayList<Double> dispServTime;

    public Statistics(ProductionManager productionManager, SelectionManager selectionManager, Buffer buffer) {
        this.productionManager = productionManager;
        this.selectionManager = selectionManager;
        this.buffer = buffer;
    }

    public double dispWaitTime() {
        double average = 0;
        double dispW = 0;
        dispWaitTime = new ArrayList<>(productionManager.getSources().size());
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            dispWaitTime.add(i,(selectionManager.getWaitTime()[i] / sourceResultList.get(i).getRequestNum()));
            average += dispWaitTime.get(i)/productionManager.getSources().size();
        }
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            dispW += Math.pow((dispWaitTime.get(i) - average),2);
        }
        return dispW/productionManager.getSources().size();
    }
    public double dispServTime() {
        double average = 0;
        double dispS = 0;
        dispServTime = new ArrayList<>(productionManager.getSources().size());
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            dispServTime.add(i,(selectionManager.getHandleTime()[i] / sourceResultList.get(i).getRequestNum()));
            average += dispServTime.get(i)/productionManager.getSources().size();
        }
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            dispS += Math.pow((dispServTime.get(i) - average),2);
        }
        return dispS/productionManager.getSources().size();
    }

    public List<SourceResult> getSourceResultList() {
        sourceResultList = new ArrayList<>(productionManager.getSources().size());
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            sourceResultList.add(new SourceResult());
            sourceResultList.get(i).setSourceNum(i);
            sourceResultList.get(i).setRequestNum(productionManager.getSources().get(i).getRequestNum() - 1);
            sourceResultList.get(i).setFailureProbability(buffer.getRejectedRequests()[i] * 1.0 / sourceResultList.get(i).getRequestNum());
            sourceResultList.get(i).setAverageWaitTime(selectionManager.getWaitTime()[i] / sourceResultList.get(i).getRequestNum());
            sourceResultList.get(i).setAverageHandleTime(selectionManager.getHandleTime()[i] / sourceResultList.get(i).getRequestNum());
            sourceResultList.get(i).setAverageTimeInSystem(sourceResultList.get(i).getAverageWaitTime() + sourceResultList.get(i).getAverageHandleTime());
        }
        return sourceResultList;
    }

    public List<DeviceResult> getDeviceResultList() {
        deviceResultList = new ArrayList<>(selectionManager.getDevices().size());
        for (int i = 0; i < selectionManager.getDevices().size(); ++i) {
            deviceResultList.add(new DeviceResult());
            deviceResultList.get(i).setDeviceNum(i);
            deviceResultList.get(i).setUtilization((selectionManager.getDevices().get(i).getReleaseTime() -
                    selectionManager.getDevices().get(i).getDowntime()) /
                    selectionManager.getLastReleaseTime());
        }
        return deviceResultList;
    }

    public List<BufferResult> getBufferCurrState() {
        bufferCurrState = new ArrayList<>(buffer.getBufferSize());
        for (int i = 0; i < buffer.getRequestQueue().size(); ++i) {
            bufferCurrState.add(new BufferResult());
            bufferCurrState.get(i).setPosition(i);
            bufferCurrState.get(i).setGenTime(buffer.getRequestQueue().get(i).getGenerationTime());
            bufferCurrState.get(i).setSourceNum(buffer.getRequestQueue().get(i).getSourceNumber());
            bufferCurrState.get(i).setRequestNum(buffer.getRequestQueue().get(i).getRequestNumber());
        }
        return bufferCurrState;
    }

    public List<SourceResult> getSourcesCurrState() {
        sourcesCurrState = new ArrayList<>(productionManager.getSources().size());
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            sourcesCurrState.add(new SourceResult());
            sourcesCurrState.get(i).setSourceNum(i);
            sourcesCurrState.get(i).setGenTime(productionManager.getSources().get(i).getGenerationTime());
            sourcesCurrState.get(i).setReqNum(productionManager.getSources().get(i).getRequestNum());
            sourcesCurrState.get(i).setRejReqNum(buffer.getRejectedRequests()[i]);
        }
        return sourcesCurrState;
    }
    public double getAvgDeviceUsage() {
        double result = 0;
        for (int i = 0; i < selectionManager.getDevices().size(); ++i) {
            result += deviceResultList.get(i).getUtilization();
        }
        return result/selectionManager.getDevices().size();
    }

    public double getAvgSystemTime() {
        double result2 = 0;
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            result2 += sourceResultList.get(i).getAverageTimeInSystem();
        }
        return result2/productionManager.getSources().size();
    }

    public double getAvgReject() {
        double result3 = 0;
        for (int i = 0; i < productionManager.getSources().size(); ++i) {
            result3 += sourceResultList.get(i).getFailureProbability();
        }
        return result3/productionManager.getSources().size();
    }


    public List<DeviceResult> getDevicesCurrState() {
        devicesCurrState = new ArrayList<>(selectionManager.getDevices().size());
        for (int i = 0; i < selectionManager.getDevices().size(); ++i) {
            devicesCurrState.add(new DeviceResult());
            devicesCurrState.get(i).setDeviceNum(i);
            devicesCurrState.get(i).setReleaseTime(selectionManager.getDevices().get(i).getReleaseTime());
            devicesCurrState.get(i).setDowntime(selectionManager.getDevices().get(i).getDowntime());
            devicesCurrState.get(i).setSourceNum(selectionManager.getDevices().get(i).getCurrentRequest() != null ?
                    selectionManager.getDevices().get(i).getCurrentRequest().getSourceNumber() : 0);
            devicesCurrState.get(i).setRequestNum(selectionManager.getDevices().get(i).getCurrentRequest() != null ?
                    selectionManager.getDevices().get(i).getCurrentRequest().getRequestNumber() : 0);
            devicesCurrState.get(i).setGenTime(selectionManager.getDevices().get(i).getCurrentRequest() != null ?
                    selectionManager.getDevices().get(i).getCurrentRequest().getGenerationTime() : 0);
        }
        return devicesCurrState;
    }
}
