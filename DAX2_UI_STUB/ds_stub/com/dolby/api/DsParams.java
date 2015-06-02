package com.dolby.api;

public final class DsParams {

	private int x;
	private String y;
    public static DsParams[] values()
    {
        return (DsParams[])$VALUES.clone();
    }

    public static DsParams valueOf(String name)
    {
        return null;
    }

    private DsParams(String s, int i)
    {
    	x = i;
    	y = s;
        
    }

    public int toInt()
    {
        return x;
    }

    public String toString()
    {
        return y;
    }

    public static DsParams FromInt(int i)
    {
        return null;
    }

    public static DsParams FromString(String name)
    {
        return null;
    }

    public static final DsParams AudioOptimizerBandCount;
    public static final DsParams AudioOptimizerBandFrequencies;
    public static final DsParams AudioOptimizerBandGains;
    public static final DsParams AudioOptimizerChannelCount;
    public static final DsParams AudioOptimizerEnable;
    public static final DsParams AudioRegulatorBandCount;
    public static final DsParams AudioRegulatorBandFrequencies;
    public static final DsParams AudioRegulatorBandHighThresholds;
    public static final DsParams AudioRegulatorBandIsolates;
    public static final DsParams AudioRegulatorBandLowThresholds;
    public static final DsParams AudioRegulatorOverdrive;
    public static final DsParams AudioRegulatorTimbrePreservationAmount;
    public static final DsParams DialogEnhancementAmount;
    public static final DsParams DialogEnhancementDucking;
    public static final DsParams DialogEnhancementEnable;
    public static final DsParams DolbyHeadphoneReverberationGain;
    public static final DsParams DolbyHeadphoneSurroundBoost;
    public static final DsParams DolbyHeadphoneVirtualizerControl;
    public static final DsParams DolbyVirtualSpeakerAngle;
    public static final DsParams DolbyVirtualSpeakerStartFrequency;
    public static final DsParams DolbyVirtualSpeakerSurroundBoost;
    public static final DsParams DolbyVirtualSpeakerVirtualizerControl;
    public static final DsParams DolbyVolumeLevelerEnable;
    public static final DsParams DolbyVolumeLevelerInputTarget;
    public static final DsParams DolbyVolumeLevelerOutputTarget;
    public static final DsParams DolbyVolumeLevelingAmount;
    public static final DsParams DolbyVolumeModelerCalibration;
    public static final DsParams DolbyVolumeModelerEnable;
    public static final DsParams GraphicEqualizerBandCount;
    public static final DsParams GraphicEqualizerBandFrequencies;
    public static final DsParams GraphicEqualizerBandGains;
    public static final DsParams GraphicEqualizerEnable;
    public static final DsParams IntelligentEqualizerAmount;
    public static final DsParams IntelligentEqualizerBandCount;
    public static final DsParams IntelligentEqualizerBandFrequencies;
    public static final DsParams IntelligentEqualizerBandTargets;
    public static final DsParams IntelligentEqualizerEnable;
    public static final DsParams NextGenSurroundEnable;
    public static final DsParams PeakLimiterBoost;
    public static final DsParams PeakLimitingProtectionMode;
    public static final DsParams VolumeMaximizerBoost;
    public static final DsParams VolumeMaximizerEnable;
    private static final DsParams $VALUES[];

    static 
    {
        AudioOptimizerBandCount = new DsParams("AudioOptimizerBandCount", 0);
        AudioOptimizerBandFrequencies = new DsParams("AudioOptimizerBandFrequencies", 1);
        AudioOptimizerBandGains = new DsParams("AudioOptimizerBandGains", 2);
        AudioOptimizerChannelCount = new DsParams("AudioOptimizerChannelCount", 3);
        AudioOptimizerEnable = new DsParams("AudioOptimizerEnable", 4);
        AudioRegulatorBandCount = new DsParams("AudioRegulatorBandCount", 5);
        AudioRegulatorBandFrequencies = new DsParams("AudioRegulatorBandFrequencies", 6);
        AudioRegulatorBandHighThresholds = new DsParams("AudioRegulatorBandHighThresholds", 7);
        AudioRegulatorBandIsolates = new DsParams("AudioRegulatorBandIsolates", 8);
        AudioRegulatorBandLowThresholds = new DsParams("AudioRegulatorBandLowThresholds", 9);
        AudioRegulatorOverdrive = new DsParams("AudioRegulatorOverdrive", 10);
        AudioRegulatorTimbrePreservationAmount = new DsParams("AudioRegulatorTimbrePreservationAmount", 11);
        DialogEnhancementAmount = new DsParams("DialogEnhancementAmount", 12);
        DialogEnhancementDucking = new DsParams("DialogEnhancementDucking", 13);
        DialogEnhancementEnable = new DsParams("DialogEnhancementEnable", 14);
        DolbyHeadphoneReverberationGain = new DsParams("DolbyHeadphoneReverberationGain", 15);
        DolbyHeadphoneSurroundBoost = new DsParams("DolbyHeadphoneSurroundBoost", 16);
        DolbyHeadphoneVirtualizerControl = new DsParams("DolbyHeadphoneVirtualizerControl", 17);
        DolbyVirtualSpeakerAngle = new DsParams("DolbyVirtualSpeakerAngle", 18);
        DolbyVirtualSpeakerStartFrequency = new DsParams("DolbyVirtualSpeakerStartFrequency", 19);
        DolbyVirtualSpeakerSurroundBoost = new DsParams("DolbyVirtualSpeakerSurroundBoost", 20);
        DolbyVirtualSpeakerVirtualizerControl = new DsParams("DolbyVirtualSpeakerVirtualizerControl", 21);
        DolbyVolumeLevelerEnable = new DsParams("DolbyVolumeLevelerEnable", 22);
        DolbyVolumeLevelerInputTarget = new DsParams("DolbyVolumeLevelerInputTarget", 23);
        DolbyVolumeLevelerOutputTarget = new DsParams("DolbyVolumeLevelerOutputTarget", 24);
        DolbyVolumeLevelingAmount = new DsParams("DolbyVolumeLevelingAmount", 25);
        DolbyVolumeModelerCalibration = new DsParams("DolbyVolumeModelerCalibration", 26);
        DolbyVolumeModelerEnable = new DsParams("DolbyVolumeModelerEnable", 27);
        GraphicEqualizerBandCount = new DsParams("GraphicEqualizerBandCount", 28);
        GraphicEqualizerBandFrequencies = new DsParams("GraphicEqualizerBandFrequencies", 29);
        GraphicEqualizerBandGains = new DsParams("GraphicEqualizerBandGains", 30);
        GraphicEqualizerEnable = new DsParams("GraphicEqualizerEnable", 31);
        IntelligentEqualizerAmount = new DsParams("IntelligentEqualizerAmount", 32);
        IntelligentEqualizerBandCount = new DsParams("IntelligentEqualizerBandCount", 33);
        IntelligentEqualizerBandFrequencies = new DsParams("IntelligentEqualizerBandFrequencies", 34);
        IntelligentEqualizerBandTargets = new DsParams("IntelligentEqualizerBandTargets", 35);
        IntelligentEqualizerEnable = new DsParams("IntelligentEqualizerEnable", 36);
        NextGenSurroundEnable = new DsParams("NextGenSurroundEnable", 37);
        PeakLimiterBoost = new DsParams("PeakLimiterBoost", 38);
        PeakLimitingProtectionMode = new DsParams("PeakLimitingProtectionMode", 39);
        VolumeMaximizerBoost = new DsParams("VolumeMaximizerBoost", 40);
        VolumeMaximizerEnable = new DsParams("VolumeMaximizerEnable", 41);
        $VALUES = (new DsParams[] {
            AudioOptimizerBandCount, AudioOptimizerBandFrequencies, AudioOptimizerBandGains, AudioOptimizerChannelCount, AudioOptimizerEnable, AudioRegulatorBandCount, AudioRegulatorBandFrequencies, AudioRegulatorBandHighThresholds, AudioRegulatorBandIsolates, AudioRegulatorBandLowThresholds, 
            AudioRegulatorOverdrive, AudioRegulatorTimbrePreservationAmount, DialogEnhancementAmount, DialogEnhancementDucking, DialogEnhancementEnable, DolbyHeadphoneReverberationGain, DolbyHeadphoneSurroundBoost, DolbyHeadphoneVirtualizerControl, DolbyVirtualSpeakerAngle, DolbyVirtualSpeakerStartFrequency, 
            DolbyVirtualSpeakerSurroundBoost, DolbyVirtualSpeakerVirtualizerControl, DolbyVolumeLevelerEnable, DolbyVolumeLevelerInputTarget, DolbyVolumeLevelerOutputTarget, DolbyVolumeLevelingAmount, DolbyVolumeModelerCalibration, DolbyVolumeModelerEnable, GraphicEqualizerBandCount, GraphicEqualizerBandFrequencies, 
            GraphicEqualizerBandGains, GraphicEqualizerEnable, IntelligentEqualizerAmount, IntelligentEqualizerBandCount, IntelligentEqualizerBandFrequencies, IntelligentEqualizerBandTargets, IntelligentEqualizerEnable, NextGenSurroundEnable, PeakLimiterBoost, PeakLimitingProtectionMode, 
            VolumeMaximizerBoost, VolumeMaximizerEnable
        });
    }
}
