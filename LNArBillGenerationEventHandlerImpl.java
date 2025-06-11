/*
 * =====================================================================
 * Copyright 2018-2019 Fidelity National Information Services, Inc.
 * and/or its subsidiaries - All Rights Reserved worldwide.
 *
 * This document is protected under the trade secret and copyright
 * laws as the property of Fidelity National Information Services, Inc.
 * and/or its subsidiaries.
 *
 * Copying, reproduction or distribution should be limited and only to
 * employees with a "need to know" to do their job.
 * Any disclosure of this document to third parties
 * is strictly prohibited.
 * =====================================================================
 */

package com.fis.ec.lending.base.core.main.arrangement.handler.billing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fis.ec.base.core.commonclasses.CorebankDate;
import com.fis.ec.base.core.commonclasses.exceptions.BusinessException;
import com.fis.ec.base.core.commonclasses.exceptions.ConcurrentUpdateException;
import com.fis.ec.base.core.commonclasses.exceptions.NotAuthorizedException;
import com.fis.ec.base.core.commonclasses.exceptions.TechnicalFailureException;
import com.fis.ec.base.core.serverclasses.ThreadContext;
import com.fis.ec.base.core.serverclasses.connector.ConnectorService;
import com.fis.ec.base.core.serverclasses.logging.LogUtility;
import com.fis.ec.baseext.core.commonclasses.arrangement.ArrangementProductIdentification;
import com.fis.ec.baseext.core.commonclasses.common.CalculatedDateResult;
import com.fis.ec.baseext.core.commonclasses.common.DirectionOfCalculation;
import com.fis.ec.baseext.core.commonclasses.condition.sets.BusinessDayRule;
import com.fis.ec.baseext.core.commonclasses.condition.types.Condition;
import com.fis.ec.baseext.core.commonclasses.condition.types.FrequencyCondition;
import com.fis.ec.baseext.core.commonclasses.condition.types.FrequencyConditionCreate;
import com.fis.ec.baseext.core.commonclasses.organization.CalendarTypeName;
import com.fis.ec.baseext.core.commonclasses.transactioneventtype.TransactionEventType;
import com.fis.ec.baseext.core.commonclasses.transactioneventtype.TransactionEventTypeCodeIdentification;
import com.fis.ec.baseext.core.commonclasses.transactioneventtype.TransactionEventTypeIdentification;
import com.fis.ec.baseext.core.commonclasses.transactioneventtype.TransactionEventTypeInternalIdentification;
import com.fis.ec.baseext.core.contract.DateTimeCalculationsContract;
import com.fis.ec.baseext.core.contract.transaction.TransactionEventTypeContract;
import com.fis.ec.baseext.core.main.datetime.DateTimeCalculationsController;
import com.fis.ec.cape.core.arrangement.commonclasses.arrangement.Arrangement;
import com.fis.ec.cape.core.arrangement.commonclasses.arrangementidentification.ArrangementInternalIdentification;
import com.fis.ec.cape.core.arrangement.commonclasses.attribute.ArAttribute;
import com.fis.ec.cape.core.arrangement.commonclasses.condition.ArrangementConditionRelationship;
import com.fis.ec.cape.core.arrangement.commonclasses.transactionscheduleinstruction.ArTransSchdlInsAddtlDetails;
import com.fis.ec.cape.core.arrangement.commonclasses.transactionscheduleinstruction.ArTransactionScheduleInstruction;
import com.fis.ec.cape.core.arrangement.commonclasses.transactionscheduleinstruction.ArTransactionScheduleInstructionCreate;
import com.fis.ec.cape.core.arrangement.commonclasses.transactionscheduleinstruction.ArTransactionScheduleInstructionMaintain;
import com.fis.ec.cape.core.arrangement.contract.ArTransactionScheduleInstructionContract;
import com.fis.ec.cape.core.arrangement.contract.ArrangementContract;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.ScheduleAdditionalDetails;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.ScheduleDateRangeCriteria;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.TransactionSchedule;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.TransactionScheduleCreate;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.TransactionScheduleSearchCriteria;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.TransactionScheduleUnProcessSearchCriteria;
import com.fis.ec.cape.core.transaction.commonclasses.transactionschedule.sets.TransactionScheduleStatus;
import com.fis.ec.cape.core.transaction.contract.TransactionScheduleContract;
import com.fis.ec.cape.core.transaction.main.transactionschedule.TransactionScheduleController;
import com.fis.ec.cape.core.transaction.schedule.handler.TransactionScheduleProcessBaseHandlerImpl;
import com.fis.ec.lending.base.core.arrangement.LNAttributesConstants;
import com.fis.ec.lending.base.core.arrangement.billing.LNBillGenerationEventData;
import com.fis.ec.lending.base.core.commonclasses.ComponentNames;
import com.fis.ec.lending.base.core.commonclasses.billingscheduledefinition.relation.LNArBillingScheduleDefRltnp;
import com.fis.ec.lending.base.core.commonclasses.common.LNCdarValues;
import com.fis.ec.lending.base.core.commonclasses.common.LNConstants;
import com.fis.ec.lending.base.core.commonclasses.common.LNEventType;
import com.fis.ec.lending.base.core.commonclasses.transaction.JSONConverter;
import com.fis.ec.lending.base.core.commonclasses.transaction.common.LNBalanceConstants;
import com.fis.ec.lending.base.core.conditions.LNConditionsConstants;
import com.fis.ec.lending.base.core.contract.billing.billingscheduledefinition.LNArBillingScheduleRelationshipContract;
import com.fis.ec.lending.base.core.main.arrangement.LNArrangementAPRCalculator;
import com.fis.ec.lending.base.core.main.arrangement.LNArrangementHelper;
import com.fis.ec.lending.base.core.main.arrangement.billing.LNArAttribute;
import com.fis.ec.lending.base.core.main.arrangement.dateutils.LNDateCalculatorForDuration;
import com.fis.ec.lending.base.core.main.arrangement.event.helper.LNArrangementEventHelper;
import com.fis.ec.lending.base.core.main.arrangement.helper.billing.LNArBillGenerationEventHelper;
import com.fis.ec.lending.base.core.main.arrangement.helper.billing.LNArBillGenerationProcessorHelper;
import com.fis.ec.lending.base.core.main.common.LNConditionHelper;
import com.fis.ec.lending.base.core.main.common.LNDateUtil;

/**
 * Lending Arrangement Bill Generation Event Handler Implementation, customized to handle bill
 * generation events.
 *
 * <p>This implementation shall be invoked on the scheduled bill event for lending arrangements.
 *
 * <p>Class implements transaction schedule processing methods from Cape
 * TransactionScheduleProcessHandler interface.
 *
 * @author e5567136
 */
public class LNArBillGenerationEventHandlerImpl extends TransactionScheduleProcessBaseHandlerImpl {
  private static final String COMPONENT_NAME = ComponentNames.EC_LN_BASE;
  private static final String CLASS_NAME = LNArBillGenerationEventHandlerImpl.class.getName();
  private static final Logger LOG = LogManager.getLogger(CLASS_NAME);
  private static final String POSTMATURITY = "POSTMATURITY";
  private LNBillGenerationEventData billGenerationEventData = new LNBillGenerationEventData();

  @Override
  public void process(
      final ThreadContext threadContext, final TransactionSchedule transactionSchedule)
      throws BusinessException, TechnicalFailureException, ConcurrentUpdateException {
    final String methodName = "process";
    LogUtility.entering(LOG, CLASS_NAME, methodName, threadContext, COMPONENT_NAME);

    new LNArBillGenerationProcessorHelper()
        .initializeLobObjects(threadContext, transactionSchedule);
    ArrangementInternalIdentification arrangementIdentification =
        new ArrangementInternalIdentification();
    arrangementIdentification.setIdentifier(transactionSchedule.getArrangementIdentifier());

    final LNArBillGenerationEventHelper lnArBillGenEventHelper =
        new LNArBillGenerationEventHelper(threadContext);
    lnArBillGenEventHelper.processBillGeneration(
        threadContext, transactionSchedule, billGenerationEventData);

    Arrangement arrangement;
    final ArrangementContract arrangementContract =
        ConnectorService.getImplementation(threadContext, ArrangementContract.class);

    arrangement =
        arrangementContract.getArrangementInformation(
            threadContext,
            arrangementIdentification,
            threadContext.getStandardParameters().getBookingDate());

    final LNArrangementAPRCalculator calculator = new LNArrangementAPRCalculator();
    calculator.getAPRValueForArrangement(threadContext, arrangement);

    LogUtility.exiting(LOG, CLASS_NAME, methodName, threadContext, COMPONENT_NAME);
  }

  /**
   * This method is used to create a new transaction scheduled based on the status of the schedule
   * and create type in the transaction schedule profile for the arrangement transaction schedule
   * instructions.
   */
  @Override
  public void afterProcess(
      final ThreadContext threadContext,
      final TransactionSchedule transactionScheduleBeforeProcess,
      final TransactionSchedule transactionScheduleAfterProcess)
      throws BusinessException, TechnicalFailureException {
    LogUtility.entering(LOG, CLASS_NAME, "afterProcess", threadContext, ComponentNames.EC_LN_BASE);

    final Arrangement arrangement =
        (Arrangement)
            transactionScheduleBeforeProcess.getLobDomainObjects().get(LNConstants.ARRANGEMENT);
    if (transactionScheduleBeforeProcess.getStatus() == TransactionScheduleStatus.CREATED
        && transactionScheduleAfterProcess.getStatus() != TransactionScheduleStatus.REJECTED
        && !arrangement
            .getEnterpriseArrangementType()
            .equals(LNConstants.CONVENIENCE_ACCESS_LINE)) {

      final ArTransactionScheduleInstructionContract arTranscScheduleInstructionContract =
          ConnectorService.getImplementation(
              threadContext, ArTransactionScheduleInstructionContract.class);

      final ArTransactionScheduleInstruction trasactionScheduleInstruction =
          arTranscScheduleInstructionContract.getTrasactionScheduleInstruction(
              threadContext,
              transactionScheduleBeforeProcess.getTransactionScheduleIntructionIdentifier(),
              null);

      transactionScheduleBeforeProcess
          .getLobDomainObjects()
          .put(LNConstants.TRANSACTION_SCHEDULE, trasactionScheduleInstruction);

      Condition bilFreqCond =
          (Condition)
              transactionScheduleBeforeProcess
                  .getLobDomainObjects()
                  .get(LNCdarValues.BILLING_FREQUENCY.getCode());

      final FrequencyCondition frequencyCondition = (FrequencyCondition) bilFreqCond;
      final CorebankDate maxIterationDate =
          frequencyCondition.getFrequencyDefinition().getMaxIterationDate();
      String eventType = null;
      if (trasactionScheduleInstruction != null
          && trasactionScheduleInstruction.getAdditionalInformation() != null
          && (transactionScheduleBeforeProcess.getRawDate().compareTo(maxIterationDate) >= 0)) {
        eventType = LNCdarValues.POST_MATURITY_BILLING_FREQUENCY.getCode();
      } else {
        eventType = LNConditionsConstants.BILLING_FREQUENCY;
      }

      if ((null != billGenerationEventData.getCurrentPymntDueRawDate()
          && !isMaturityBill(threadContext, transactionScheduleBeforeProcess))) {
        if (trasactionScheduleInstruction != null
            && trasactionScheduleInstruction.getAdditionalInformation() != null
            && getAdditionalInfo(trasactionScheduleInstruction)) {

          createBillGenerationEventForAddtlInfo(
              threadContext,
              arrangement,
              eventType,
              transactionScheduleBeforeProcess,
              trasactionScheduleInstruction);
        } else {
          createBillGenerationEvent(
              threadContext, arrangement, eventType, transactionScheduleBeforeProcess);
        }
      }
    }

    LogUtility.exiting(LOG, CLASS_NAME, "afterProcess", threadContext, ComponentNames.EC_LN_BASE);
  }

  private boolean getAdditionalInfo(
      ArTransactionScheduleInstruction trasactionScheduleInstruction) {
    Boolean isAdditionalInfo = false;
    JSONObject additionalDetail =
        new JSONObject(trasactionScheduleInstruction.getAdditionalInformation());
    Object additionalDtlInfo = additionalDetail.get(LNConstants.ADDITIONAL_DETAILS);
    if (additionalDtlInfo != null) {
      isAdditionalInfo = true;
    }
    return isAdditionalInfo;
  }

  /**
   * @param threadContext
   * @param arrangement
   * @param transactionScheduleBeforeProcess
   * @param transactionSchedule
   * @throws TechnicalFailureException
   * @throws BusinessException
   * @throws NotAuthorizedException
   * @throws ConcurrentUpdateException
   */
  private void createBillGenerationEvent(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final String eventType,
      final TransactionSchedule transactionSchedule)
      throws TechnicalFailureException, BusinessException {
    LogUtility.entering(
        LOG, CLASS_NAME, "createBillGenerationEvent", threadContext, ComponentNames.EC_LN_BASE);

    final LNArBillGenerationProcessorHelper lnBilGenProcessorHelper =
        new LNArBillGenerationProcessorHelper();
    final ArTransactionScheduleInstructionCreate arTransactionScheduleInstructionCreate =
        prepareBillGenerationTransSchdlCreate(
            threadContext, arrangement, eventType, transactionSchedule);
    BigDecimal amount = BigDecimal.ZERO;
    BigDecimal amountValue =
        lnBilGenProcessorHelper.getBalance(
            LNBalanceConstants.OUTSTANDING_PRINCIPLE_BALANCE, transactionSchedule);
    if (amountValue != null) {
      amount = amountValue;
    }
    if (amount.compareTo(BigDecimal.ZERO) == 0
        && arrangement.getSecondaryStatus().equals(LNEventType.CHARGE_OFF)) {
      lnBilGenProcessorHelper.getBalance(
          LNBalanceConstants.CHARGE_OFF_PRINCIPAL_BALANCE, transactionSchedule);
    }

    if (arTransactionScheduleInstructionCreate != null) {
      final ArTransactionScheduleInstructionContract arTranscScheduleInstructionContract =
          ConnectorService.getImplementation(
              threadContext, ArTransactionScheduleInstructionContract.class);
      TransactionSchedule existingSchedule =
          isTransactionScheduleExist(
              threadContext,
              arrangement,
              arTransactionScheduleInstructionCreate.getFrequencyConditionCreate(),
              LNEventType.BILL_GENERATION);
      final ArrangementInternalIdentification arrangementIdentification =
          new ArrangementInternalIdentification(arrangement.getArrangementIdentifier());
      final LNArBillingScheduleRelationshipContract lnArBillingScheduleRelationshipContract =
          ConnectorService.getImplementation(
              threadContext, LNArBillingScheduleRelationshipContract.class);
      List<LNArBillingScheduleDefRltnp> billingScheduleList =
          lnArBillingScheduleRelationshipContract.getApprovedArrBillingSchdlRlntpList(
              threadContext, arrangementIdentification, null);
      if (existingSchedule != null && billingScheduleList.size() > 1) {
        final ArTransactionScheduleInstructionMaintain arSchdlTranInsMaintain =
            prepareTransSchdlBlsIdfrUpdate(
                threadContext,
                arrangement,
                arTransactionScheduleInstructionCreate,
                existingSchedule,
                LNEventType.BILL_GENERATION);

        try {
          arTranscScheduleInstructionContract.updateTrasactionScheduleInstruction(
              threadContext, arSchdlTranInsMaintain);
        } catch (ConcurrentUpdateException e) {
          e.printStackTrace();
        }
      } else {
        arTranscScheduleInstructionContract.createTrasactionScheduleInstruction(
            threadContext, arTransactionScheduleInstructionCreate);
      }
    }

    LogUtility.exiting(
        LOG, CLASS_NAME, "createBillGenerationEvent", threadContext, ComponentNames.EC_LN_BASE);
  }

  private void createBillGenerationEventForAddtlInfo(
      ThreadContext threadContext,
      Arrangement arrangement,
      String eventType,
      TransactionSchedule transactionScheduleBeforeProcess,
      ArTransactionScheduleInstruction trasactionScheduleInstruction)
      throws BusinessException, TechnicalFailureException {
    JSONObject additionalDetail =
        new JSONObject(trasactionScheduleInstruction.getAdditionalInformation());
    JSONArray jsonArrayList =
        additionalDetail
            .getJSONObject(LNConstants.ADDITIONAL_DETAILS)
            .optJSONArray(LNConstants.BLS_IDFR);
    List<Object> scheduleAdditionalDetails = jsonArrayList.toList();
    if (scheduleAdditionalDetails.size() <= 1) {
      LNArBillingScheduleDefRltnp billingSchedule =
          (LNArBillingScheduleDefRltnp)
              transactionScheduleBeforeProcess.getLobDomainObjects().get(LNConstants.BILL_SCHEDULE);
      final LNConditionHelper lnConditionHelper = new LNConditionHelper(threadContext);
      if (billingSchedule != null) {
        Condition billingFrequency =
            lnConditionHelper.getConditionFrequency(
                billingSchedule.getBillingFrequencyIdentifier(),
                null,
                arrangement,
                LNCdarValues.BILLING_FREQUENCY.getCode());
        Condition paymentFrequency =
            lnConditionHelper.getConditionFrequency(
                billingSchedule.getPaymentFrequencyIdentifier(),
                null,
                arrangement,
                LNCdarValues.PAYMENT_FREQUENCY.getCode());

        if (billingFrequency != null) {
          transactionScheduleBeforeProcess
              .getLobDomainObjects()
              .put(LNCdarValues.BILLING_FREQUENCY.getCode(), billingFrequency);
        }
        if (paymentFrequency != null) {
          transactionScheduleBeforeProcess
              .getLobDomainObjects()
              .put(LNCdarValues.PAYMENT_FREQUENCY.getCode(), paymentFrequency);
        }
        transactionScheduleBeforeProcess
            .getLobDomainObjects()
            .put(
                LNConstants.UPDATED_BILLING_FREQUENCY,
                (Condition)
                    transactionScheduleBeforeProcess
                        .getLobDomainObjects()
                        .get(LNCdarValues.BILLING_FREQUENCY.getCode()));
      }
      createBillGenerationEvent(
          threadContext, arrangement, eventType, transactionScheduleBeforeProcess);
    } else {
      createBillGenerationEventForConvergeBill(
          threadContext, arrangement, eventType, transactionScheduleBeforeProcess);
    }
  }

  private void createBillGenerationEventForConvergeBill(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final String eventType,
      final TransactionSchedule transactionSchedule)
      throws TechnicalFailureException, BusinessException {
    final ArrangementInternalIdentification arrangementIdentification =
        new ArrangementInternalIdentification(arrangement.getArrangementIdentifier());
    final LNArBillingScheduleRelationshipContract lnArBillingScheduleRelationshipContract =
        ConnectorService.getImplementation(
            threadContext, LNArBillingScheduleRelationshipContract.class);
    List<LNArBillingScheduleDefRltnp> billingScheduleList =
        lnArBillingScheduleRelationshipContract.getApprovedArrBillingSchdlRlntpList(
            threadContext, arrangementIdentification, null);
    for (LNArBillingScheduleDefRltnp billingSchedule : billingScheduleList) {
      transactionSchedule.getLobDomainObjects().put(LNConstants.BILL_SCHEDULE, billingSchedule);

      final LNConditionHelper lnConditionHelper = new LNConditionHelper(threadContext);
      Condition billingFrequency =
          lnConditionHelper.getConditionFrequency(
              billingSchedule.getBillingFrequencyIdentifier(),
              null,
              arrangement,
              LNCdarValues.BILLING_FREQUENCY.getCode());
      Condition paymentFrequency =
          lnConditionHelper.getConditionFrequency(
              billingSchedule.getPaymentFrequencyIdentifier(),
              null,
              arrangement,
              LNCdarValues.PAYMENT_FREQUENCY.getCode());

      if (billingFrequency != null) {
        transactionSchedule
            .getLobDomainObjects()
            .put(LNCdarValues.BILLING_FREQUENCY.getCode(), billingFrequency);
      }
      if (paymentFrequency != null) {
        transactionSchedule
            .getLobDomainObjects()
            .put(LNCdarValues.PAYMENT_FREQUENCY.getCode(), paymentFrequency);
      }
      transactionSchedule
          .getLobDomainObjects()
          .put(
              LNConstants.UPDATED_BILLING_FREQUENCY,
              (Condition)
                  transactionSchedule
                      .getLobDomainObjects()
                      .get(LNCdarValues.BILLING_FREQUENCY.getCode()));

      createBillGenerationEvent(threadContext, arrangement, eventType, transactionSchedule);
    }
  }

  private ArTransactionScheduleInstructionCreate prepareBillGenerationTransSchdlCreate(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final String eventType,
      final TransactionSchedule transactionSchedule)
      throws BusinessException, TechnicalFailureException {
    LogUtility.entering(
        LOG,
        CLASS_NAME,
        "Start preparePaymentEventTransSchdlCreate",
        threadContext,
        ComponentNames.EC_LN_BASE);

    ArTransactionScheduleInstructionCreate arTransSchdlInstructCreate = null;
    final ArrangementInternalIdentification arrangementIdentification =
        new ArrangementInternalIdentification(arrangement.getArrangementIdentifier());

    Condition bilFreqCond = (Condition) transactionSchedule.getLobDomainObjects().get(eventType);

    /*
     * Take the updated billing frequency which is set at time of next bill date calculation
     * This frequency contains the updated freq if the freq is change by PDC extn
     * */
    if (!eventType.equals(LNCdarValues.POST_MATURITY_BILLING_FREQUENCY.getCode())) {
      bilFreqCond =
          (Condition)
              transactionSchedule.getLobDomainObjects().get(LNConstants.UPDATED_BILLING_FREQUENCY);
    }

    final CorebankDate currentPymntDueRawDate = billGenerationEventData.getCurrentPymntDueRawDate();
    final FrequencyCondition frequencyCondition = (FrequencyCondition) bilFreqCond;

    /* Using maturity bill lead days if present in product to calculate
    maturity bill generation event date  for the second last bill processing event*/
    int maturityLeadDays =
        new LNArBillGenerationProcessorHelper()
            .getMaturityBillLeadDays(threadContext, transactionSchedule, currentPymntDueRawDate);

    if (maturityLeadDays != 0) {
      frequencyCondition.getFrequencyDefinition().setSecondaryUnits(maturityLeadDays);
    }

    resetBusinessDayBillDtBeforeDueDt(
        threadContext, arrangement, currentPymntDueRawDate, bilFreqCond);

    final CorebankDate maxIterationDate =
        frequencyCondition.getFrequencyDefinition().getMaxIterationDate();

    ArTransactionScheduleInstruction trasactionScheduleInstruction =
        (ArTransactionScheduleInstruction)
            transactionSchedule.getLobDomainObjects().get(LNConstants.TRANSACTION_SCHEDULE);

    boolean matBillGenFlag =
        getMatBillGenFlag(
            threadContext, arrangement, bilFreqCond, currentPymntDueRawDate, maturityLeadDays);

    if (currentPymntDueRawDate.before(maxIterationDate) && !matBillGenFlag) {
      arTransSchdlInstructCreate =
          prepareTransSchdlInst(
              threadContext,
              eventType,
              arrangementIdentification,
              bilFreqCond,
              currentPymntDueRawDate,
              transactionSchedule,
              trasactionScheduleInstruction);
      if (eventType.equals(LNCdarValues.BILLING_FREQUENCY.getCode())
          && trasactionScheduleInstruction != null) {
        final ArTransSchdlInsAddtlDetails arTransInsScheDtl = new ArTransSchdlInsAddtlDetails();
        final Map<String, Object> arTransSchdlInsAddtlAttribute = new HashMap<>();

        LNArBillingScheduleDefRltnp billingSchedule =
            (LNArBillingScheduleDefRltnp)
                transactionSchedule.getLobDomainObjects().get(LNConstants.BILL_SCHEDULE);
        final Map<String, Object> value = new HashMap<>();
        List<Object> additionalDetailList = new ArrayList<>();
        final Map<String, Object> additionalDetail = new HashMap<>();
        final JSONConverter gsonJsonDataConverterPluginImpl = new JSONConverter();
        additionalDetailList.add(billingSchedule.getBillingScheduleIdentifier());
        value.put(LNConstants.BLS_IDFR, additionalDetailList);
        additionalDetail.put(LNConstants.ADDITIONAL_DETAILS, value);
        String additionalDetailsJson = gsonJsonDataConverterPluginImpl.toJson(additionalDetail);
        arTransSchdlInsAddtlAttribute.put(LNCdarValues.BILLING.getCode(), additionalDetailsJson);
        arTransInsScheDtl.setArTransSchdlInsAddtlAttribute(arTransSchdlInsAddtlAttribute);
        arTransSchdlInstructCreate.setArTransSchdlInsAddtlDetails(arTransInsScheDtl);
      }
    } else {
      if (transactionSchedule.getAdditionalInformation() == null
          || !transactionSchedule.getAdditionalInformation().contains("MaturityBill")) {
        generateOddDaysMaturityBill(
            threadContext,
            arrangement,
            transactionSchedule,
            bilFreqCond,
            currentPymntDueRawDate,
            maturityLeadDays);
      }
    }

    return arTransSchdlInstructCreate;
  }

  private boolean getMatBillGenFlag(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      Condition bilFreqCond,
      final CorebankDate currentPymntDueRawDate,
      int maturityLeadDays)
      throws BusinessException, TechnicalFailureException {
    LNArBillGenerationEventHelper arBillGenerationEventHelper =
        new LNArBillGenerationEventHelper(threadContext);
    boolean is2ndLastBill = false;
    CorebankDate maturityDate = null;
    final ArAttribute matAttribute =
        arBillGenerationEventHelper.getAttributeValue(
            threadContext,
            arrangement,
            LNConstants.MATURITYDATE,
            threadContext.getStandardParameters().getBookingDate());
    if (matAttribute != null && !matAttribute.getAttributeValues().isEmpty()) {
      maturityDate = (CorebankDate) matAttribute.getAttributeValues().get(0).getValue();
    }
    is2ndLastBill =
        arBillGenerationEventHelper.isPenultimateSchedule(
            threadContext,
            arrangement,
            bilFreqCond,
            currentPymntDueRawDate,
            maturityLeadDays,
            is2ndLastBill,
            maturityDate);
    return is2ndLastBill;
  }

  private void generateOddDaysMaturityBill(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final TransactionSchedule transactionSchedule,
      Condition bilFreqCond,
      final CorebankDate currentPymntDueRawDate,
      int maturityLeadDays)
      throws BusinessException, TechnicalFailureException {
    LNArBillGenerationEventHelper arBillGenerationEventHelper =
        new LNArBillGenerationEventHelper(threadContext);
    boolean is2ndLastBill = false;
    CorebankDate maturityDate = null;
    final ArAttribute matAttribute =
        arBillGenerationEventHelper.getAttributeValue(
            threadContext,
            arrangement,
            LNConstants.MATURITYDATE,
            threadContext.getStandardParameters().getBookingDate());
    if (matAttribute != null && !matAttribute.getAttributeValues().isEmpty()) {
      maturityDate = (CorebankDate) matAttribute.getAttributeValues().get(0).getValue();
    }
    is2ndLastBill =
        arBillGenerationEventHelper.isPenultimateSchedule(
            threadContext,
            arrangement,
            bilFreqCond,
            currentPymntDueRawDate,
            maturityLeadDays,
            is2ndLastBill,
            maturityDate);
    if (is2ndLastBill) {
      CorebankDate rawBillGenDte =
          DateTimeCalculationsController.getInstance()
              .getDate(
                  threadContext,
                  maturityDate,
                  DirectionOfCalculation.BACKWARD.getCode(),
                  maturityLeadDays,
                  0,
                  0);
      CorebankDate lstBillGenDte = rawBillGenDte;
      if (!LNDateUtil.isBusinessDay(
          threadContext,
          arrangement.getOrganizationUnitIdentifier(),
          rawBillGenDte,
          CalendarTypeName.PROCESSING)) {
        final LNConditionHelper lnConditionHelper = new LNConditionHelper(threadContext);
        final ArrangementInternalIdentification arrangementIdentification =
            new ArrangementInternalIdentification(arrangement.getArrangementIdentifier());
        final ArrangementConditionRelationship billingFrequency =
            lnConditionHelper.getConditionDetail(
                threadContext,
                arrangementIdentification,
                LNCdarValues.BILLING_FREQUENCY.getCode(),
                threadContext.getStandardParameters().getBookingDate());
        FrequencyCondition frequencyCondition = null;
        if (billingFrequency != null) {
          frequencyCondition = (FrequencyCondition) billingFrequency.getCondition();
        }
        lstBillGenDte =
            LNDateCalculatorForDuration.applyBusinessDayRule(
                threadContext,
                rawBillGenDte,
                frequencyCondition.getFrequencyDefinition().getBusinessDayRule(),
                arrangement.getOrganizationUnitIdentifier());
      }
      if (lstBillGenDte.compareTo(threadContext.getStandardParameters().getBookingDate()) > 0) {
        createTransSchdlEvent(
            threadContext, arrangement, transactionSchedule, lstBillGenDte, rawBillGenDte);
      } else {
        createTransSchdlEvent(
            threadContext,
            arrangement,
            transactionSchedule,
            threadContext.getStandardParameters().getBookingDate(),
            threadContext.getStandardParameters().getBookingDate());
      }
    }
  }

  private void createTransSchdlEvent(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final TransactionSchedule transactionSchedule,
      CorebankDate effDte,
      CorebankDate rawDte)
      throws BusinessException, TechnicalFailureException {
    TransactionScheduleCreate transactionScheduleCreate = new TransactionSchedule();
    transactionScheduleCreate.setArrangementIdentifier(
        transactionSchedule.getArrangementIdentifier());
    transactionScheduleCreate.setTransactionIdentifier(
        transactionSchedule.getTransactionIdentifier());
    transactionScheduleCreate.setTransactionEventTypeIdentifier(
        transactionSchedule.getTransactionEventTypeIdentifier());
    if (transactionSchedule.getScheduleAdditionalDetails() != null
        && !transactionSchedule
            .getScheduleAdditionalDetails()
            .getScheduleAdditionalAttribute()
            .isEmpty()) {
      transactionSchedule
          .getScheduleAdditionalDetails()
          .getScheduleAdditionalAttribute()
          .put("BillType", "MaturityBill");
      transactionScheduleCreate.setScheduleAdditionalDetails(
          transactionSchedule.getScheduleAdditionalDetails());
    } else {
      final ScheduleAdditionalDetails scheduleAdditionalDetails = new ScheduleAdditionalDetails();
      final Map<String, Object> scheduleAdditionalAttribute = new HashMap<>();
      scheduleAdditionalAttribute.put("BillType", "MaturityBill");
      scheduleAdditionalDetails.setScheduleAdditionalAttribute(scheduleAdditionalAttribute);
      transactionScheduleCreate.setScheduleAdditionalDetails(scheduleAdditionalDetails);
    }
    transactionScheduleCreate.setEffectiveDate(effDte);
    transactionScheduleCreate.setRawDate(rawDte);
    transactionScheduleCreate.setPrcsGrpIdfr(arrangement.getProcessingGroupId());
    new TransactionScheduleController()
        .createTransactionSchedule(threadContext, transactionScheduleCreate);

    final List<TransactionSchedule> transactionSchduleBillGeneration =
        getTransactionSchedule(threadContext, arrangement, LNEventType.BILL_GENERATION);
    if (transactionSchduleBillGeneration != null && !transactionSchduleBillGeneration.isEmpty()) {
      if (transactionSchduleBillGeneration.get(0).getAdditionalInformation() != null
          && transactionSchduleBillGeneration
              .get(0)
              .getAdditionalInformation()
              .contains("MaturityBill")) {
        try {
          final TransactionScheduleContract transactionScheduleContract =
              ConnectorService.getImplementation(threadContext, TransactionScheduleContract.class);

          Timestamp processingStartTimestamp = threadContext.getProcessingStartTimestamp();
          Timestamp value = LNDateUtil.addMilisecondsToTimestamp(processingStartTimestamp, 1000);
          threadContext.setProcessingStartTimestamp(value);

          transactionScheduleContract.processTransactionSchedule(
              threadContext,
              new ArrangementInternalIdentification(arrangement.getArrangementIdentifier()),
              transactionSchduleBillGeneration.get(0).getTransactionIdentifier());

        } catch (ConcurrentUpdateException e) {
          throw new TechnicalFailureException(e);
        }
      }
    }
  }

  public List<TransactionSchedule> getTransactionSchedule(
      final ThreadContext threadContext, final Arrangement arrangement, final String eventType)
      throws TechnicalFailureException, BusinessException {
    final ArrangementInternalIdentification arrIdentification =
        new ArrangementInternalIdentification(arrangement.getArrangementIdentifier());
    final LNArrangementEventHelper lnArrangementEventHelper =
        new LNArrangementEventHelper(threadContext);
    final TransactionEventType transactionEvent =
        lnArrangementEventHelper.getEventType(threadContext, eventType);

    final TransactionEventTypeIdentification transEvtTypeIdntfctn =
        new TransactionEventTypeInternalIdentification(transactionEvent.getIdentifier());

    final TransactionScheduleContract transactionScheduleContract =
        ConnectorService.getImplementation(threadContext, TransactionScheduleContract.class);
    final TransactionScheduleUnProcessSearchCriteria transSchdlUnPrcsSrchCrtria =
        new TransactionScheduleUnProcessSearchCriteria();
    transSchdlUnPrcsSrchCrtria.setTransactionEventTypeIdentification(transEvtTypeIdntfctn);
    transSchdlUnPrcsSrchCrtria.setAsOfDate(threadContext.getStandardParameters().getBookingDate());
    return transactionScheduleContract.getUnProcessedTransactionScheduleByArrangement(
        threadContext, arrIdentification, transSchdlUnPrcsSrchCrtria);
  }
  /**
   * @param threadContext
   * @param eventType
   * @param arTransSchdlInstructCreate
   * @param arrangementIdentification
   * @param bilFreqCond
   * @param rawNextTransSchdlStartDateToSkip
   * @return
   * @throws TechnicalFailureException
   * @throws BusinessException
   */
  private ArTransactionScheduleInstructionCreate prepareTransSchdlInst(
      final ThreadContext threadContext,
      final String eventType,
      final ArrangementInternalIdentification arrangementIdentification,
      final Condition bilFreqCond,
      final CorebankDate rawNextTransSchdlStartDateToSkip,
      final TransactionSchedule transactionSchedule,
      ArTransactionScheduleInstruction trasactionScheduleInstruction)
      throws TechnicalFailureException {
    ArTransactionScheduleInstructionCreate arTransSchdlInstructCreate =
        new ArTransactionScheduleInstructionCreate();
    if (bilFreqCond != null) {
      arTransSchdlInstructCreate.setArrangmentIdentification(arrangementIdentification);
      arTransSchdlInstructCreate.setEffectiveDate(rawNextTransSchdlStartDateToSkip);

      final FrequencyCondition frequencyCondition = (FrequencyCondition) bilFreqCond;
      frequencyCondition.getFrequencyDefinition().setStartDate(rawNextTransSchdlStartDateToSkip);

      arTransSchdlInstructCreate.setFrequencyConditionCreate(frequencyCondition);

      final TransactionEventTypeCodeIdentification transactionEventType =
          new TransactionEventTypeCodeIdentification();
      transactionEventType.setCode(LNEventType.BILL_GENERATION);

      arTransSchdlInstructCreate.setTransactionEventTypeIdentification(transactionEventType);
      arTransSchdlInstructCreate.setSettlementArrangmentIdentification(arrangementIdentification);

      if (eventType.equals(LNCdarValues.POST_MATURITY_BILLING_FREQUENCY.getCode())) {
        final ArTransSchdlInsAddtlDetails arTransInsScheDtl = new ArTransSchdlInsAddtlDetails();
        final Map<String, Object> arTransSchdlInsAddtlAttribute = new HashMap<>();
        final Map<String, Object> value = new HashMap<>();
        final Map<String, Object> additionalDetail = new HashMap<>();
        final JSONConverter gsonJsonDataConverterPluginImpl = new JSONConverter();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put(POSTMATURITY, LNConstants.YESVALUE);
        value.put(POSTMATURITY, additionalData);
        if (trasactionScheduleInstruction != null) {
          LNArBillingScheduleDefRltnp billingSchedule =
              (LNArBillingScheduleDefRltnp)
                  transactionSchedule.getLobDomainObjects().get(LNConstants.BILL_SCHEDULE);
          List<Object> additionalDetailList = new ArrayList<>();
          additionalDetailList.add(billingSchedule.getBillingScheduleIdentifier());
          value.put(LNConstants.BLS_IDFR, additionalDetailList);
        }
        additionalDetail.put(LNConstants.ADDITIONAL_DETAILS, value);
        String additionalDetailsJson = gsonJsonDataConverterPluginImpl.toJson(additionalDetail);
        arTransSchdlInsAddtlAttribute.put(POSTMATURITY, additionalDetailsJson);
        arTransInsScheDtl.setArTransSchdlInsAddtlAttribute(arTransSchdlInsAddtlAttribute);
        arTransSchdlInstructCreate.setArTransSchdlInsAddtlDetails(arTransInsScheDtl);
      }

      LogUtility.exiting(
          LOG,
          CLASS_NAME,
          "End prepareRateResetTransSchdlCreate",
          threadContext,
          ComponentNames.EC_LN_BASE);
    }
    return arTransSchdlInstructCreate;
  }

  public boolean isMaturityBill(
      final ThreadContext threadContext, final TransactionSchedule transactionSchedule)
      throws BusinessException, TechnicalFailureException {
    boolean isMaturityBill = false;
    CorebankDate currentScheduleEndDate = billGenerationEventData.getCurrentPymntDueRawDate();
    final LNArAttribute lnArAtrribute =
        (LNArAttribute)
            transactionSchedule.getLobDomainObjects().get(LNAttributesConstants.MATURITY_DATE);
    final CorebankDate maturityDate = CorebankDate.valueOf(lnArAtrribute.getValue());
    currentScheduleEndDate =
        new LNArBillGenerationProcessorHelper()
            .getEffectiveBussinessDay(threadContext, transactionSchedule, currentScheduleEndDate);
    if (currentScheduleEndDate.compareTo(maturityDate) == 0) {
      isMaturityBill = true;
    }
    return isMaturityBill;
  }

  public void resetBusinessDayBillDtBeforeDueDt(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final CorebankDate rawNextPaymentSchdlDate,
      final Condition bilFreqCond)
      throws TechnicalFailureException, BusinessException {

    CorebankDate nextDate = null;
    final DateTimeCalculationsContract dateTimeContract =
        ConnectorService.getImplementation(threadContext, DateTimeCalculationsContract.class);
    ArrangementProductIdentification arPrdIdfn =
        new LNArrangementHelper(threadContext).getArrangementProductIdentification(arrangement);

    if (bilFreqCond != null) {
      final FrequencyCondition frequencyCondition = (FrequencyCondition) bilFreqCond;
      frequencyCondition.getFrequencyDefinition().setStartDate(rawNextPaymentSchdlDate);

      CalculatedDateResult calculatedDate =
          dateTimeContract.getDate(
              threadContext,
              frequencyCondition.getFrequencyDefinition(),
              arrangement.getOrganizationUnitIdentifier(),
              arrangement.getCurrencyCode(),
              CalendarTypeName.PROCESSING,
              arPrdIdfn);

      if (calculatedDate != null) {
        nextDate = calculatedDate.getAdjustedDate();
      }
      /*set Business day rule if nextDate is before and equal Current Cycle Payment Due Date*/
      if (nextDate == null) {
        frequencyCondition
            .getFrequencyDefinition()
            .setBusinessDayRule(BusinessDayRule.NEXT_BUSINESS_DAY);
      }
    }
  }

  public TransactionSchedule isMultipleBillGenerationEvent(
      final ThreadContext threadContext,
      final ArrangementInternalIdentification arrangementIdentification,
      TransactionSchedule transactionSchedule)
      throws TechnicalFailureException, BusinessException {
    TransactionSchedule multipleSchedule = null;
    LNArBillGenerationProcessorHelper billGenerationHelper =
        new LNArBillGenerationProcessorHelper();
    final TransactionScheduleContract transactionScheduleContract =
        ConnectorService.getImplementation(threadContext, TransactionScheduleContract.class);
    TransactionScheduleSearchCriteria searchCriteria = new TransactionScheduleSearchCriteria();
    ScheduleDateRangeCriteria scheduleDate = new ScheduleDateRangeCriteria();
    scheduleDate.setFromDate(threadContext.getStandardParameters().getBookingDate());
    scheduleDate.setToDate(threadContext.getStandardParameters().getBookingDate());
    searchCriteria.setScheduleDateRange(scheduleDate);
    final TransactionEventTypeIdentification eventTypeId =
        new TransactionEventTypeCodeIdentification(LNEventType.BILL_GENERATION);
    searchCriteria.setTransactionEventTypeIdentification(eventTypeId);
    final List<TransactionSchedule> transactionScheduleList =
        transactionScheduleContract.getListTransactionScheduleByArrangement(
            threadContext, arrangementIdentification, searchCriteria);
    Arrangement arrangement =
        (Arrangement) transactionSchedule.getLobDomainObjects().get(LNConstants.ARRANGEMENT);
    if (transactionScheduleList != null && transactionScheduleList.size() > 1) {
      for (TransactionSchedule schdule : transactionScheduleList) {
        if (schdule.getTransactionIdentifier() != transactionSchedule.getTransactionIdentifier()) {
          multipleSchedule = schdule;
          LNArBillingScheduleDefRltnp billingSchedule =
              billGenerationHelper.getBillingScheduleDef(
                  threadContext, multipleSchedule, arrangement);
          final LNConditionHelper lnConditionHelper = new LNConditionHelper(threadContext);
          Condition billingFrequency =
              lnConditionHelper.getConditionFrequency(
                  billingSchedule.getBillingFrequencyIdentifier(),
                  null,
                  arrangement,
                  LNCdarValues.BILLING_FREQUENCY.getCode());
          transactionSchedule
              .getLobDomainObjects()
              .put(LNConstants.MULTIPLE_FREQUENCY, billingFrequency);
          Condition paymentFrequency =
              lnConditionHelper.getConditionFrequency(
                  billingSchedule.getPaymentFrequencyIdentifier(),
                  null,
                  arrangement,
                  LNCdarValues.PAYMENT_FREQUENCY.getCode());

          if (billingFrequency != null) {
            multipleSchedule
                .getLobDomainObjects()
                .put(LNCdarValues.BILLING_FREQUENCY.getCode(), billingFrequency);
          }
          if (paymentFrequency != null) {
            multipleSchedule
                .getLobDomainObjects()
                .put(LNCdarValues.PAYMENT_FREQUENCY.getCode(), paymentFrequency);
          }
        }
      }
    }
    return multipleSchedule;
  }

  public TransactionSchedule isTransactionScheduleExist(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final FrequencyConditionCreate frequencyConditionCreate,
      final String eventType)
      throws TechnicalFailureException, BusinessException {
    TransactionSchedule schedule = null;
    CorebankDate startDate = frequencyConditionCreate.getFrequencyDefinition().getStartDate();
    ArrangementProductIdentification identification = new ArrangementProductIdentification();
    final CalculatedDateResult calculatedDateResult =
        LNDateUtil.getDateByFrequency(
            threadContext,
            frequencyConditionCreate.getFrequencyDefinition(),
            arrangement.getOrganizationUnitIdentifier(),
            identification);
    if (calculatedDateResult != null) {
      startDate = calculatedDateResult.getAdjustedDate();
    }
    ArrangementInternalIdentification arrangementIdentification =
        new ArrangementInternalIdentification();
    arrangementIdentification.setIdentifier(arrangement.getArrangementIdentifier());
    final TransactionScheduleContract transactionScheduleContract =
        ConnectorService.getImplementation(threadContext, TransactionScheduleContract.class);
    TransactionScheduleSearchCriteria searchCriteria = new TransactionScheduleSearchCriteria();
    ScheduleDateRangeCriteria scheduleDate = new ScheduleDateRangeCriteria();
    scheduleDate.setFromDate(startDate);
    scheduleDate.setToDate(startDate);
    final TransactionEventTypeIdentification eventTypeId =
        new TransactionEventTypeCodeIdentification(eventType);
    searchCriteria.setTransactionEventTypeIdentification(eventTypeId);
    searchCriteria.setScheduleDateRange(scheduleDate);
    final List<TransactionSchedule> transactionScheduleList =
        transactionScheduleContract.getListTransactionScheduleByArrangement(
            threadContext, arrangementIdentification, searchCriteria);
    if (transactionScheduleList != null && !transactionScheduleList.isEmpty()) {
      schedule = transactionScheduleList.get(0);
    }
    return schedule;
  }

  public ArTransactionScheduleInstructionMaintain prepareTransSchdlBlsIdfrUpdate(
      final ThreadContext threadContext,
      final Arrangement arrangement,
      final ArTransactionScheduleInstructionCreate arTrnsSchdlInstrctnCrt,
      final TransactionSchedule existingSchedule,
      final String eventType)
      throws TechnicalFailureException, BusinessException {
    final ArTransactionScheduleInstructionContract arTranscScheduleInstructionContract =
        ConnectorService.getImplementation(
            threadContext, ArTransactionScheduleInstructionContract.class);

    final ArTransactionScheduleInstruction trasactionScheduleInstruction =
        arTranscScheduleInstructionContract.getTrasactionScheduleInstruction(
            threadContext,
            existingSchedule.getTransactionScheduleIntructionIdentifier(),
            arTrnsSchdlInstrctnCrt.getEffectiveDate());

    final ArrangementInternalIdentification arrangementIdentification =
        new ArrangementInternalIdentification(arrangement.getArrangementIdentifier());

    final ArTransactionScheduleInstructionMaintain arTransactionScheduleInstructionMaintain =
        new ArTransactionScheduleInstructionMaintain();
    arTransactionScheduleInstructionMaintain.setArrangmentIdentification(arrangementIdentification);
    arTransactionScheduleInstructionMaintain.setEffectiveDate(
        arTrnsSchdlInstrctnCrt.getEffectiveDate());

    final JSONConverter jsonConvertor = new JSONConverter();
    List<Object> additionalDetailList = new ArrayList<>();
    if (trasactionScheduleInstruction.getAdditionalInformation() != null) {
      JSONObject additionalDetail =
          new JSONObject(trasactionScheduleInstruction.getAdditionalInformation());
      JSONArray jsonArrayList =
          additionalDetail
              .getJSONObject(LNConstants.ADDITIONAL_DETAILS)
              .optJSONArray(LNConstants.BLS_IDFR);
      List<Object> additionalDtl = jsonArrayList.toList();
      additionalDetailList.add(Long.parseLong(additionalDtl.get(0).toString()));
    }
    if (arTrnsSchdlInstrctnCrt.getArTransSchdlInsAddtlDetails().getArTransSchdlInsAddtlAttribute()
        != null) {
      for (Object value :
          arTrnsSchdlInstrctnCrt
              .getArTransSchdlInsAddtlDetails()
              .getArTransSchdlInsAddtlAttribute()
              .values()) {
        Map<String, Object> additionalDtlMap =
            (Map<String, Object>)
                jsonConvertor.toMapObject(value.toString()).get(LNConstants.ADDITIONAL_DETAILS);
        List<Object> additionalDtl = (List<Object>) additionalDtlMap.get(LNConstants.BLS_IDFR);
        additionalDetailList.add(Long.parseLong(additionalDtl.get(0).toString()));
      }
    }
    final Map<String, Object> additionalDetail = new HashMap<>();

    Map<String, Object> value = new HashMap<>();
    final JSONConverter gsonJsonDataConverterPluginImpl = new JSONConverter();
    value.put(LNConstants.BLS_IDFR, additionalDetailList);
    additionalDetail.put(LNConstants.ADDITIONAL_DETAILS, value);
    final Map<String, Object> arTransSchdlInsAddtlAttribute = new HashMap<>();
    String additionalDetails = gsonJsonDataConverterPluginImpl.toJson(additionalDetail);
    arTransSchdlInsAddtlAttribute.put(LNCdarValues.BILLING.getCode(), additionalDetails);
    final ArTransSchdlInsAddtlDetails arTransSchdlInsAddtlDetails =
        new ArTransSchdlInsAddtlDetails();
    arTransSchdlInsAddtlDetails.setArTransSchdlInsAddtlAttribute(arTransSchdlInsAddtlAttribute);
    arTransactionScheduleInstructionMaintain.setArTransSchdlInsAddtlDetails(
        arTransSchdlInsAddtlDetails);
    // get the existing instruction
    final TransactionEventTypeContract transactionEventTypeContact =
        ConnectorService.getImplementation(threadContext, TransactionEventTypeContract.class);
    final TransactionEventTypeCodeIdentification transactionEventType =
        new TransactionEventTypeCodeIdentification();
    if (transactionEventTypeContact != null) {

      if (arTrnsSchdlInstrctnCrt.getFrequencyConditionCreate() != null) {
        final FrequencyCondition frequencyCondition =
            (FrequencyCondition) arTrnsSchdlInstrctnCrt.getFrequencyConditionCreate();
        arTransactionScheduleInstructionMaintain.setFrequencyConditionMaintain(frequencyCondition);
      }
      transactionEventType.setCode(eventType);
      arTransactionScheduleInstructionMaintain.setIdentifier(
          trasactionScheduleInstruction.getIdentifier());

      arTransactionScheduleInstructionMaintain.setTransactionEventTypeIdentification(
          transactionEventType);
    }
    return arTransactionScheduleInstructionMaintain;
  }
}
