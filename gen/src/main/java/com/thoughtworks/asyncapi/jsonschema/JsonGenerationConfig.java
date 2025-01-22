package com.thoughtworks.asyncapi.jsonschema;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.InclusionLevel;
import org.jsonschema2pojo.SourceSortOrder;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;
import picocli.CommandLine;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class JsonGenerationConfig implements GenerationConfig {
  private static final DefaultGenerationConfig DEFAULT_GENERATION_CONFIG = new DefaultGenerationConfig() {
  };
  @CommandLine.Option(names = "--json-generateBuilder")
  @Parameter
  private boolean generateBuilders = DEFAULT_GENERATION_CONFIG.isGenerateBuilders();
  @CommandLine.Option(names = "--json-includeTypeInfo")
  @Parameter
  private boolean includeTypeInfo = DEFAULT_GENERATION_CONFIG.isIncludeTypeInfo();
  @CommandLine.Option(names = "--json-includeConstructorPropertiesAnnotation")
  @Parameter
  private boolean includeConstructorPropertiesAnnotation = DEFAULT_GENERATION_CONFIG.isIncludeConstructorPropertiesAnnotation();
  @CommandLine.Option(names = "--json-usePrimitives")
  @Parameter
  private boolean usePrimitives = DEFAULT_GENERATION_CONFIG.isUsePrimitives();
  @CommandLine.Option(names = "--json-source")
  @Parameter
  private Iterator<URL> source;
  @CommandLine.Option(names = "--json-targetDirectory")
  private File targetDirectory = DEFAULT_GENERATION_CONFIG.getTargetDirectory();
  @CommandLine.Option(names = "--json-targetPackage")
  private String targetPackage = DEFAULT_GENERATION_CONFIG.getTargetPackage();
  @CommandLine.Option(names = "--json-propertyWordDelimiters")
  @Parameter
  private char[] propertyWordDelimiters = DEFAULT_GENERATION_CONFIG.getPropertyWordDelimiters();
  @CommandLine.Option(names = "--json-useLongIntegers")
  @Parameter
  private boolean useLongIntegers = DEFAULT_GENERATION_CONFIG.isUseLongIntegers();
  @CommandLine.Option(names = "--json-useBigIntegers")
  @Parameter
  private boolean useBigIntegers = DEFAULT_GENERATION_CONFIG.isUseBigIntegers();
  @CommandLine.Option(names = "--json-useDoubleNumbers")
  @Parameter
  private boolean useDoubleNumbers = DEFAULT_GENERATION_CONFIG.isUseDoubleNumbers();
  @CommandLine.Option(names = "--json-useBigDecimals")
  @Parameter
  private boolean useBigDecimals = DEFAULT_GENERATION_CONFIG.isUseBigDecimals();
  @CommandLine.Option(names = "--json-includeHashcodeAndEquals")
  @Parameter
  private boolean includeHashcodeAndEquals = DEFAULT_GENERATION_CONFIG.isIncludeHashcodeAndEquals();
  @CommandLine.Option(names = "--json-includeToString")
  @Parameter
  private boolean includeToString = DEFAULT_GENERATION_CONFIG.isIncludeToString();
  @CommandLine.Option(names = "--json-toStringExcludes")
  @Parameter
  private String[] toStringExcludes = DEFAULT_GENERATION_CONFIG.getToStringExcludes();
  @CommandLine.Option(names = "--json-annotationStyle")
  @Parameter
  private AnnotationStyle annotationStyle = DEFAULT_GENERATION_CONFIG.getAnnotationStyle();
  @CommandLine.Option(names = "--json-useTitleAsClassname")
  @Parameter
  private boolean useTitleAsClassname = DEFAULT_GENERATION_CONFIG.isUseTitleAsClassname();
  @CommandLine.Option(names = "--json-inclusionLevel")
  @Parameter
  private InclusionLevel inclusionLevel = DEFAULT_GENERATION_CONFIG.getInclusionLevel();
  @CommandLine.Option(names = "--json-customAnnotator")
  @Parameter
  private Class<? extends Annotator> customAnnotator = DEFAULT_GENERATION_CONFIG.getCustomAnnotator();
  @CommandLine.Option(names = "--json-customRuleFactory")
  @Parameter
  private Class<? extends RuleFactory> customRuleFactory = DEFAULT_GENERATION_CONFIG.getCustomRuleFactory();
  @CommandLine.Option(names = "--json-includeJsr303Annotations")
  @Parameter
  private boolean includeJsr303Annotations = DEFAULT_GENERATION_CONFIG.isIncludeJsr303Annotations();
  @CommandLine.Option(names = "--json-includeJsr305Annotations")
  @Parameter
  private boolean includeJsr305Annotations = DEFAULT_GENERATION_CONFIG.isIncludeJsr305Annotations();
  @CommandLine.Option(names = "--json-useOptionalForGetters")
  @Parameter
  private boolean useOptionalForGetters = DEFAULT_GENERATION_CONFIG.isUseOptionalForGetters();
  @CommandLine.Option(names = "--json-sourceType")
  @Parameter
  private SourceType sourceType = DEFAULT_GENERATION_CONFIG.getSourceType();
  @CommandLine.Option(names = "--json-removeOldOutput")
  @Parameter
  private boolean removeOldOutput = DEFAULT_GENERATION_CONFIG.isRemoveOldOutput();
  @CommandLine.Option(names = "--json-outputEncoding")
  @Parameter
  private String outputEncoding = DEFAULT_GENERATION_CONFIG.getOutputEncoding();
  @CommandLine.Option(names = "--json-useJodaDates")
  @Parameter
  private boolean useJodaDates = DEFAULT_GENERATION_CONFIG.isUseJodaDates();
  @CommandLine.Option(names = "--json-useJodaLocalDates")
  @Parameter
  private boolean useJodaLocalDates = DEFAULT_GENERATION_CONFIG.isUseJodaLocalDates();
  @CommandLine.Option(names = "--json-useJodaLocalTimes")
  @Parameter
  private boolean useJodaLocalTimes = DEFAULT_GENERATION_CONFIG.isUseJodaLocalTimes();
  @CommandLine.Option(names = "--json-parcelable")
  @Parameter
  private boolean parcelable = DEFAULT_GENERATION_CONFIG.isParcelable();
  @CommandLine.Option(names = "--json-serializable")
  @Parameter
  private boolean serializable = DEFAULT_GENERATION_CONFIG.isSerializable();
  @CommandLine.Option(names = "--json-fileFilter")
  @Parameter
  private String fileFilterString;
  private FileFilter fileFilter = DEFAULT_GENERATION_CONFIG.getFileFilter();
  @CommandLine.Option(names = "--json-initializeCollections")
  @Parameter
  private boolean initializeCollections = DEFAULT_GENERATION_CONFIG.isInitializeCollections();
  @CommandLine.Option(names = "--json-classNamePrefix")
  @Parameter
  private String classNamePrefix = DEFAULT_GENERATION_CONFIG.getClassNamePrefix();
  @CommandLine.Option(names = "--json-classNameSuffix")
  @Parameter
  private String classNameSuffix = DEFAULT_GENERATION_CONFIG.getClassNameSuffix();
  @CommandLine.Option(names = "--json-fileExtensions")
  @Parameter
  private String[] fileExtensions = DEFAULT_GENERATION_CONFIG.getFileExtensions();
  @CommandLine.Option(names = "--json-includeConstructors")
  @Parameter
  private boolean includeConstructors = DEFAULT_GENERATION_CONFIG.isIncludeConstructors();
  @CommandLine.Option(names = "--json-constructorsRequiredPropertiesOnly")
  @Parameter
  private boolean constructorsRequiredPropertiesOnly = DEFAULT_GENERATION_CONFIG.isConstructorsRequiredPropertiesOnly();
  @CommandLine.Option(names = "--json-includeRequiredPropertiesConstructor")
  @Parameter
  private boolean includeRequiredPropertiesConstructor = DEFAULT_GENERATION_CONFIG.isIncludeRequiredPropertiesConstructor();
  @CommandLine.Option(names = "--json-includeAllPropertiesConstructor")
  @Parameter
  private boolean includeAllPropertiesConstructor = DEFAULT_GENERATION_CONFIG.isIncludeAllPropertiesConstructor();
  @CommandLine.Option(names = "--json-includeCopyConstructor")
  @Parameter
  private boolean includeCopyConstructor = DEFAULT_GENERATION_CONFIG.isIncludeCopyConstructor();
  @CommandLine.Option(names = "--json-includeAdditionalProperties")
  @Parameter
  private boolean includeAdditionalProperties = DEFAULT_GENERATION_CONFIG.isIncludeAdditionalProperties();
  @CommandLine.Option(names = "--json-includeGetters")
  @Parameter
  private boolean includeGetters = DEFAULT_GENERATION_CONFIG.isIncludeGetters();
  @CommandLine.Option(names = "--json-includeSetters")
  @Parameter
  private boolean includeSetters = DEFAULT_GENERATION_CONFIG.isIncludeSetters();
  @CommandLine.Option(names = "--json-targetVersion")
  @Parameter
  private String targetVersion = DEFAULT_GENERATION_CONFIG.getTargetVersion();
  @CommandLine.Option(names = "--json-includeDynamicAccessors")
  @Parameter
  private boolean includeDynamicAccessors = DEFAULT_GENERATION_CONFIG.isIncludeDynamicAccessors();
  @CommandLine.Option(names = "--json-includeDynamicGetters")
  @Parameter
  private boolean includeDynamicGetters = DEFAULT_GENERATION_CONFIG.isIncludeDynamicGetters();
  @CommandLine.Option(names = "--json-includeDynamicSetters")
  @Parameter
  private boolean includeDynamicSetters = DEFAULT_GENERATION_CONFIG.isIncludeDynamicSetters();
  @CommandLine.Option(names = "--json-includeDynamicBuilders")
  @Parameter
  private boolean includeDynamicBuilders = DEFAULT_GENERATION_CONFIG.isIncludeDynamicBuilders();
  @CommandLine.Option(names = "--json-dateTimeType")
  @Parameter
  private String dateTimeType = DEFAULT_GENERATION_CONFIG.getDateTimeType();
  @CommandLine.Option(names = "--json-dateType")
  @Parameter
  private String dateType = DEFAULT_GENERATION_CONFIG.getDateType();
  @CommandLine.Option(names = "--json-timeType")
  @Parameter
  private String timeType = DEFAULT_GENERATION_CONFIG.getTimeType();
  @CommandLine.Option(names = "--json-formatDates")
  @Parameter
  private boolean formatDates = DEFAULT_GENERATION_CONFIG.isFormatDates();
  @CommandLine.Option(names = "--json-formatTimes")
  @Parameter
  private boolean formatTimes = DEFAULT_GENERATION_CONFIG.isFormatTimes();
  @CommandLine.Option(names = "--json-formatDateTimes")
  @Parameter
  private boolean formatDateTimes = DEFAULT_GENERATION_CONFIG.isFormatDateTimes();
  @CommandLine.Option(names = "--json-customDatePattern")
  @Parameter
  private String customDatePattern = DEFAULT_GENERATION_CONFIG.getCustomDatePattern();
  @CommandLine.Option(names = "--json-customTimePattern")
  @Parameter
  private String customTimePattern = DEFAULT_GENERATION_CONFIG.getCustomTimePattern();
  @CommandLine.Option(names = "--json-customDateTimePattern")
  @Parameter
  private String customDateTimePattern = DEFAULT_GENERATION_CONFIG.getCustomDateTimePattern();
  @CommandLine.Option(names = "--json-refFragmentPathDelimiters")
  @Parameter
  private String refFragmentPathDelimiters = DEFAULT_GENERATION_CONFIG.getRefFragmentPathDelimiters();
  @CommandLine.Option(names = "--json-sourceSortOrder")
  @Parameter
  private SourceSortOrder sourceSortOrder = DEFAULT_GENERATION_CONFIG.getSourceSortOrder();
  @CommandLine.Option(names = "--json-formatTypeMapping")
  @Parameter
  private Map<String, String> formatTypeMapping = DEFAULT_GENERATION_CONFIG.getFormatTypeMapping();
  @CommandLine.Option(names = "--json-includeGeneratedAnnotation")
  @Parameter
  private boolean includeGeneratedAnnotation = DEFAULT_GENERATION_CONFIG.isIncludeGeneratedAnnotation();
  @CommandLine.Option(names = "--json-useJakartaValidation")
  @Parameter
  private boolean useJakartaValidation = DEFAULT_GENERATION_CONFIG.isUseJakartaValidation();

  @CommandLine.Option(names = "--java-classNamePrefix")
  @Parameter
  private String javaClassNamePrefix = "";

  public String getJavaClassNamePrefix() {
    return javaClassNamePrefix;
  }

  public void setJavaClassNamePrefix(String javaClassNamePrefix) {
    this.javaClassNamePrefix = javaClassNamePrefix;
  }

  public boolean isExpandAllOf() {
    return expandAllOf;
  }

  public void setExpandAllOf(boolean expandAllOf) {
    this.expandAllOf = expandAllOf;
  }

  @CommandLine.Option(names = "--json-expand-allOf")
  @Parameter
  private boolean expandAllOf = false;




  @Override
  public boolean isGenerateBuilders() {
    return generateBuilders;
  }

  public void setGenerateBuilders(boolean generateBuilders) {
    this.generateBuilders = generateBuilders;
  }

  @Override
  public boolean isIncludeTypeInfo() {
    return includeTypeInfo;
  }

  public void setIncludeTypeInfo(boolean includeTypeInfo) {
    this.includeTypeInfo = includeTypeInfo;
  }

  @Override
  public boolean isIncludeConstructorPropertiesAnnotation() {
    return includeConstructorPropertiesAnnotation;
  }

  public void setIncludeConstructorPropertiesAnnotation(boolean includeConstructorPropertiesAnnotation) {
    this.includeConstructorPropertiesAnnotation = includeConstructorPropertiesAnnotation;
  }

  @Override
  public boolean isUsePrimitives() {
    return usePrimitives;
  }

  public void setUsePrimitives(boolean usePrimitives) {
    this.usePrimitives = usePrimitives;
  }

  @Override
  public Iterator<URL> getSource() {
    return source;
  }

  public void setSource(Iterator<URL> source) {
    this.source = source;
  }

  @Override
  public File getTargetDirectory() {
    return targetDirectory;
  }

  public void setTargetDirectory(File targetDirectory) {
    this.targetDirectory = targetDirectory;
  }

  @Override
  public String getTargetPackage() {
    return targetPackage;
  }

  public void setTargetPackage(String targetPackage) {
    this.targetPackage = targetPackage;
  }

  @Override
  public char[] getPropertyWordDelimiters() {
    return propertyWordDelimiters;
  }

  public void setPropertyWordDelimiters(char[] propertyWordDelimiters) {
    this.propertyWordDelimiters = propertyWordDelimiters;
  }

  @Override
  public boolean isUseLongIntegers() {
    return useLongIntegers;
  }

  public void setUseLongIntegers(boolean useLongIntegers) {
    this.useLongIntegers = useLongIntegers;
  }

  @Override
  public boolean isUseBigIntegers() {
    return useBigIntegers;
  }

  public void setUseBigIntegers(boolean useBigIntegers) {
    this.useBigIntegers = useBigIntegers;
  }

  @Override
  public boolean isUseDoubleNumbers() {
    return useDoubleNumbers;
  }

  public void setUseDoubleNumbers(boolean useDoubleNumbers) {
    this.useDoubleNumbers = useDoubleNumbers;
  }

  @Override
  public boolean isUseBigDecimals() {
    return useBigDecimals;
  }

  public void setUseBigDecimals(boolean useBigDecimals) {
    this.useBigDecimals = useBigDecimals;
  }

  @Override
  public boolean isIncludeHashcodeAndEquals() {
    return includeHashcodeAndEquals;
  }

  public void setIncludeHashcodeAndEquals(boolean includeHashcodeAndEquals) {
    this.includeHashcodeAndEquals = includeHashcodeAndEquals;
  }

  @Override
  public boolean isIncludeToString() {
    return includeToString;
  }

  public void setIncludeToString(boolean includeToString) {
    this.includeToString = includeToString;
  }

  @Override
  public String[] getToStringExcludes() {
    return toStringExcludes;
  }

  public void setToStringExcludes(String[] toStringExcludes) {
    this.toStringExcludes = toStringExcludes;
  }

  @Override
  public AnnotationStyle getAnnotationStyle() {
    return annotationStyle;
  }

  public void setAnnotationStyle(AnnotationStyle annotationStyle) {
    this.annotationStyle = annotationStyle;
  }

  @Override
  public boolean isUseTitleAsClassname() {
    return useTitleAsClassname;
  }

  public void setUseTitleAsClassname(boolean useTitleAsClassname) {
    this.useTitleAsClassname = useTitleAsClassname;
  }

  @Override
  public InclusionLevel getInclusionLevel() {
    return inclusionLevel;
  }

  public void setInclusionLevel(InclusionLevel inclusionLevel) {
    this.inclusionLevel = inclusionLevel;
  }

  @Override
  public Class<? extends Annotator> getCustomAnnotator() {
    return customAnnotator;
  }

  public void setCustomAnnotator(Class<? extends Annotator> customAnnotator) {
    this.customAnnotator = customAnnotator;
  }

  @Override
  public Class<? extends RuleFactory> getCustomRuleFactory() {
    return customRuleFactory;
  }

  public void setCustomRuleFactory(Class<? extends RuleFactory> customRuleFactory) {
    this.customRuleFactory = customRuleFactory;
  }

  @Override
  public boolean isIncludeJsr303Annotations() {
    return includeJsr303Annotations;
  }

  public void setIncludeJsr303Annotations(boolean includeJsr303Annotations) {
    this.includeJsr303Annotations = includeJsr303Annotations;
  }

  @Override
  public boolean isIncludeJsr305Annotations() {
    return includeJsr305Annotations;
  }

  public void setIncludeJsr305Annotations(boolean includeJsr305Annotations) {
    this.includeJsr305Annotations = includeJsr305Annotations;
  }

  @Override
  public boolean isUseOptionalForGetters() {
    return useOptionalForGetters;
  }

  public void setUseOptionalForGetters(boolean useOptionalForGetters) {
    this.useOptionalForGetters = useOptionalForGetters;
  }

  @Override
  public SourceType getSourceType() {
    return sourceType;
  }

  public void setSourceType(SourceType sourceType) {
    this.sourceType = sourceType;
  }

  @Override
  public boolean isRemoveOldOutput() {
    return removeOldOutput;
  }

  public void setRemoveOldOutput(boolean removeOldOutput) {
    this.removeOldOutput = removeOldOutput;
  }

  @Override
  public String getOutputEncoding() {
    return outputEncoding;
  }

  public void setOutputEncoding(String outputEncoding) {
    this.outputEncoding = outputEncoding;
  }

  @Override
  public boolean isUseJodaDates() {
    return useJodaDates;
  }

  public void setUseJodaDates(boolean useJodaDates) {
    this.useJodaDates = useJodaDates;
  }

  @Override
  public boolean isUseJodaLocalDates() {
    return useJodaLocalDates;
  }

  public void setUseJodaLocalDates(boolean useJodaLocalDates) {
    this.useJodaLocalDates = useJodaLocalDates;
  }

  @Override
  public boolean isUseJodaLocalTimes() {
    return useJodaLocalTimes;
  }

  public void setUseJodaLocalTimes(boolean useJodaLocalTimes) {
    this.useJodaLocalTimes = useJodaLocalTimes;
  }

  @Override
  public boolean isParcelable() {
    return parcelable;
  }

  public void setParcelable(boolean parcelable) {
    this.parcelable = parcelable;
  }

  @Override
  public boolean isSerializable() {
    return serializable;
  }

  public void setSerializable(boolean serializable) {
    this.serializable = serializable;
  }

  @Override
  public FileFilter getFileFilter() {
    return fileFilter;
  }

  public void setFileFilter(FileFilter fileFilter) {
    this.fileFilter = fileFilter;
  }

  @Override
  public boolean isInitializeCollections() {
    return initializeCollections;
  }

  public void setInitializeCollections(boolean initializeCollections) {
    this.initializeCollections = initializeCollections;
  }

  @Override
  public String getClassNamePrefix() {
    return classNamePrefix;
  }

  public void setClassNamePrefix(String classNamePrefix) {
    this.classNamePrefix = classNamePrefix;
  }

  @Override
  public String getClassNameSuffix() {
    return classNameSuffix;
  }

  public void setClassNameSuffix(String classNameSuffix) {
    this.classNameSuffix = classNameSuffix;
  }

  @Override
  public String[] getFileExtensions() {
    return fileExtensions;
  }

  public void setFileExtensions(String[] fileExtensions) {
    this.fileExtensions = fileExtensions;
  }

  @Override
  public boolean isIncludeConstructors() {
    return includeConstructors;
  }

  public void setIncludeConstructors(boolean includeConstructors) {
    this.includeConstructors = includeConstructors;
  }

  @Override
  public boolean isConstructorsRequiredPropertiesOnly() {
    return constructorsRequiredPropertiesOnly;
  }

  public void setConstructorsRequiredPropertiesOnly(boolean constructorsRequiredPropertiesOnly) {
    this.constructorsRequiredPropertiesOnly = constructorsRequiredPropertiesOnly;
  }

  @Override
  public boolean isIncludeRequiredPropertiesConstructor() {
    return includeRequiredPropertiesConstructor;
  }

  public void setIncludeRequiredPropertiesConstructor(boolean includeRequiredPropertiesConstructor) {
    this.includeRequiredPropertiesConstructor = includeRequiredPropertiesConstructor;
  }

  @Override
  public boolean isIncludeAllPropertiesConstructor() {
    return includeAllPropertiesConstructor;
  }

  public void setIncludeAllPropertiesConstructor(boolean includeAllPropertiesConstructor) {
    this.includeAllPropertiesConstructor = includeAllPropertiesConstructor;
  }

  @Override
  public boolean isIncludeCopyConstructor() {
    return includeCopyConstructor;
  }

  public void setIncludeCopyConstructor(boolean includeCopyConstructor) {
    this.includeCopyConstructor = includeCopyConstructor;
  }

  @Override
  public boolean isIncludeAdditionalProperties() {
    return includeAdditionalProperties;
  }

  public void setIncludeAdditionalProperties(boolean includeAdditionalProperties) {
    this.includeAdditionalProperties = includeAdditionalProperties;
  }

  @Override
  public boolean isIncludeGetters() {
    return includeGetters;
  }

  public void setIncludeGetters(boolean includeGetters) {
    this.includeGetters = includeGetters;
  }

  @Override
  public boolean isIncludeSetters() {
    return includeSetters;
  }

  public void setIncludeSetters(boolean includeSetters) {
    this.includeSetters = includeSetters;
  }

  @Override
  public String getTargetVersion() {
    return targetVersion;
  }

  public void setTargetVersion(String targetVersion) {
    this.targetVersion = targetVersion;
  }

  @Override
  public boolean isIncludeDynamicAccessors() {
    return includeDynamicAccessors;
  }

  public void setIncludeDynamicAccessors(boolean includeDynamicAccessors) {
    this.includeDynamicAccessors = includeDynamicAccessors;
  }

  @Override
  public boolean isIncludeDynamicGetters() {
    return includeDynamicGetters;
  }

  public void setIncludeDynamicGetters(boolean includeDynamicGetters) {
    this.includeDynamicGetters = includeDynamicGetters;
  }

  @Override
  public boolean isIncludeDynamicSetters() {
    return includeDynamicSetters;
  }

  public void setIncludeDynamicSetters(boolean includeDynamicSetters) {
    this.includeDynamicSetters = includeDynamicSetters;
  }

  @Override
  public boolean isIncludeDynamicBuilders() {
    return includeDynamicBuilders;
  }

  public void setIncludeDynamicBuilders(boolean includeDynamicBuilders) {
    this.includeDynamicBuilders = includeDynamicBuilders;
  }

  @Override
  public String getDateTimeType() {
    return dateTimeType;
  }

  public void setDateTimeType(String dateTimeType) {
    this.dateTimeType = dateTimeType;
  }

  @Override
  public String getDateType() {
    return dateType;
  }

  public void setDateType(String dateType) {
    this.dateType = dateType;
  }

  @Override
  public String getTimeType() {
    return timeType;
  }

  public void setTimeType(String timeType) {
    this.timeType = timeType;
  }

  @Override
  public boolean isFormatDates() {
    return formatDates;
  }

  public void setFormatDates(boolean formatDates) {
    this.formatDates = formatDates;
  }

  @Override
  public boolean isFormatTimes() {
    return formatTimes;
  }

  public void setFormatTimes(boolean formatTimes) {
    this.formatTimes = formatTimes;
  }

  @Override
  public boolean isFormatDateTimes() {
    return formatDateTimes;
  }

  public void setFormatDateTimes(boolean formatDateTimes) {
    this.formatDateTimes = formatDateTimes;
  }

  @Override
  public String getCustomDatePattern() {
    return customDatePattern;
  }

  public void setCustomDatePattern(String customDatePattern) {
    this.customDatePattern = customDatePattern;
  }

  @Override
  public String getCustomTimePattern() {
    return customTimePattern;
  }

  public void setCustomTimePattern(String customTimePattern) {
    this.customTimePattern = customTimePattern;
  }

  @Override
  public String getCustomDateTimePattern() {
    return customDateTimePattern;
  }

  public void setCustomDateTimePattern(String customDateTimePattern) {
    this.customDateTimePattern = customDateTimePattern;
  }

  @Override
  public String getRefFragmentPathDelimiters() {
    return refFragmentPathDelimiters;
  }

  public void setRefFragmentPathDelimiters(String refFragmentPathDelimiters) {
    this.refFragmentPathDelimiters = refFragmentPathDelimiters;
  }

  @Override
  public SourceSortOrder getSourceSortOrder() {
    return sourceSortOrder;
  }

  public void setSourceSortOrder(SourceSortOrder sourceSortOrder) {
    this.sourceSortOrder = sourceSortOrder;
  }

  @Override
  public Map<String, String> getFormatTypeMapping() {
    return formatTypeMapping;
  }

  public void setFormatTypeMapping(Map<String, String> formatTypeMapping) {
    this.formatTypeMapping = formatTypeMapping;
  }

  @Override
  public boolean isIncludeGeneratedAnnotation() {
    return includeGeneratedAnnotation;
  }

  public void setIncludeGeneratedAnnotation(boolean includeGeneratedAnnotation) {
    this.includeGeneratedAnnotation = includeGeneratedAnnotation;
  }

  @Override
  public boolean isUseJakartaValidation() {
    return useJakartaValidation;
  }

  public void setUseJakartaValidation(boolean useJakartaValidation) {
    this.useJakartaValidation = useJakartaValidation;
  }


}
