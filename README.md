It will help to learn and go through Java Concepts :)


/*
 * =====================================================================
 * Copyright 2016-2019 Fidelity National Information Services, Inc.
 * and/or its subsidiaries - All Rights Reserved worldwide.
 *
 * This document is protected under the trade secret and copyright laws
 * as the property of Fidelity National Information Services, Inc.
 * and/or its subsidiaries.
 *
 * Copying, reproduction or distribution should be limited and only to
 * employees with a "need to know" to do their job. Any disclosure
 * of this document to third parties is strictly prohibited.
 * =====================================================================
*/

import { Injectable } from '@angular/core';
import { of } from "rxjs";
import { Observable } from 'rxjs/internal/Observable';
import { map } from "rxjs/operators";
import { CustomerBannerService } from '../../../base/services/home/customer-banner.service';
import { BasicDetailsData, IArrangementInput, RoleInfo, TermTypeValBscDtls } from '../../../shared/account-enquiry.interface';
import { ACCOUNT_DETAIL_RETRIEVE_URI, RETRIEVE_LOC_UNSECURED_LOAN_ACCOUNT_URI, RETRIEVE_BNPL_LOAN_ACCOUNT_URI, RETRIEVE_SECURED_LOAN_ACCOUNT_URI, RETRIVE_PAYMENT_SCHEDULE_URI } from '../../../base/services/http-base/http-constants';
import { HttpBaseService, IParams } from '../../../base/services/http-base/http-base.service';
import { DataShareService } from '../../../base/services/data-share/data-share.service';
import { LendingConstants } from '../../../shared/constants/lending-constants.model';
import { AccountdetailsService } from './accountdetails.service';
import { catchError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { MsgHandlerService } from '../../../base/services/msg-handler/msg-handler.service';

export enum refreshBasicDetailOptns {
  REFRESH_DATA_WITHOUT_ROLETABLE = 1,
  REFRESH_DATA_WITH_ROLETABLE,
  DONT_REFRESH
}

@Injectable({
  providedIn: 'root'
})

export class BasicdetailsService {
  private _basicDetailsData: BasicDetailsData;
  private accountBasicDetails: BasicDetailsData;
  private accountID: number;
  private _rolesTable = [];
  basicDetalsobj: BasicDetailsData = {} as BasicDetailsData;
  private errList = [];
  constructor(private httpService: HttpBaseService,
    private msgHandlerService: MsgHandlerService,
    private customerService: CustomerBannerService,
    private accountdetailsService: AccountdetailsService,

    private dataShareService: DataShareService
  ) { }

  getPymntSched(accountId) {
    const pathParams: IParams[] = [
      { 'name': 'accountId', 'value': accountId }
    ];
    const queryParams =   { 'accountType': 'UnsecuredInstalmentLoan' };
    return this.httpService.mbpAPIHttpRequest("GET", RETRIVE_PAYMENT_SCHEDULE_URI, null, queryParams, pathParams).pipe(catchError(async (error: HttpErrorResponse) =>  {
      this.errList.push(error);
      this.mgsDisplay();
    }));
  }

  getBasicDetails(requestData: IArrangementInput, refreshDataOptn?: refreshBasicDetailOptns): Observable<BasicDetailsData> {
    let basicdtls: any;
    if ((refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITHOUT_ROLETABLE) ||
      (refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE) || (!this._basicDetailsData)) {
      basicdtls = this.getArrangementDetails(requestData).pipe(
        map((data) => {
          this.dataShareService.setAppData('AR_RETRIEVE_RESPONSE', data);
          this._populateBasicDetails(data, refreshDataOptn);
          this.setAccountNumber(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.arId);
          return this._basicDetailsData;
        }));
      return basicdtls;
    }
    else {
      return of(this._basicDetailsData);
    }

  }

  getSecBasicDetails(requestData: IArrangementInput, refreshDataOptn?: refreshBasicDetailOptns): Observable<BasicDetailsData> {
    let basicdtls: any;
    if ((refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITHOUT_ROLETABLE) ||
      (refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE) || (!this._basicDetailsData)) {
      basicdtls = this.getSecArrangementDetails(requestData).pipe(
        map((data) => {
          this.dataShareService.setAppData('AR_RETRIEVE_RESPONSE', data);
          this._populateSecBasicDetails(data, refreshDataOptn);
          this.setAccountNumber(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.arId);
          return this._basicDetailsData;
        }));
      return basicdtls;
    }
    else {
      return of(this._basicDetailsData);
    }

  }

  getAccountBasicDetails(requestData: IArrangementInput): Observable<BasicDetailsData> {
    let basicdtls: any;
    basicdtls = this.getArrangementDetails(requestData).pipe(
      map((data) => {
        if(data){
          this.dataShareService.setAppData('AR_RETRIEVE_RESPONSE', data);
          this._populateBasicDetails(data, refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE);
          this.setAccountNumber(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.arId);
        }
        return this._basicDetailsData;
      }));
    return basicdtls;
  }

  getLOCAccountBasicDetails(requestData: IArrangementInput): Observable<BasicDetailsData> {
    let basicdtls: any;
    basicdtls = this.getLOCArrangementDetails(requestData).pipe(
      map((data) => {
        this.dataShareService.setAppData('AR_RETRIEVE_RESPONSE', data);
        this._populateLOCBasicDetails(data, refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE);
        this.setAccountNumber(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.arId);
        return this._basicDetailsData;
      }));
    return basicdtls;
  }
  getLOCArrangementDetails(requestData: IArrangementInput): Observable<any> {
    requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls.map(element => element.arId.arId = element.arId && element.arId.arId ? element.arId.arId.toUpperCase() : null);
    if ((requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.UNSECURED_LOC_AR_TYPE) ||(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.UNSECURED_CAL_AR_TYPE)) {
      return this.httpService.postHttpRequest(RETRIEVE_LOC_UNSECURED_LOAN_ACCOUNT_URI, requestData).pipe(catchError(async (error: HttpErrorResponse) =>  {
        this.errList.push(error);
        this.mgsDisplay();
      }));
    }

  }
  public _populateLOCBasicDetails(data, refreshDataOptn?: refreshBasicDetailOptns) {
    let lecaccountId;
    data.entity.arDtls[0] && data.entity.arDtls[0].locLnBscDtlAndAdtTrl && data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl && data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl.extAcctId && data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls ?
      data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls.forEach((arid: any) => {
        if (arid.acctIdTyp == LendingConstants.LEGACYACCTNUM) {
          lecaccountId = arid.acctId;
        }
      }) : '';
    this.basicDetalsobj = {
      originalTermValue: data?.entity?.arDtls?.[0]?.locLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermVal,
      originalTermType: data?.entity?.arDtls?.[0]?.locLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermTyp,
      originalMaturityDate: data?.entity?.arDtls?.[0]?.locLnBusDtl?.matInfo?.origMatDte,
      customerCode: '',
      termVal: null,
      rateResetMaturityRecal: data?.entity?.arDtls?.[0]?.locLnBusDtl?.arPmtDtlAndAdtTrl?.rteChgDtl?.rteRstMatReCalcOpt,
      rateChangeFrequency: null,
      rateChangeEffctveDate: null,
      effectiveDate: data?.entity?.arDtls?.[0]?.locLnBscDtlAndAdtTrl?.arBscDtl?.effDte,
      primaryStatus: data?.entity?.arDtls?.[0]?.locLnBscDtlAndAdtTrl?.arBscDtl?.prmyStat,
      rolesTable: [],
      acctId: lecaccountId ? lecaccountId : '',
      cnvrsDte: data?.entity?.arDtls?.[0]?.locLnBscDtlAndAdtTrl?.cnvrsDte,
      advFromOrgntnInd: data?.entity?.arDtls?.[0]?.locLnBusDtl?.arDisbInfoAndAdtTrl?.advFromOrgntnInd,
      autoDisbAlwInd: data?.entity?.arDtls?.[0]?.locLnBusDtl?.arDisbInfoAndAdtTrl?.autoDisbAlwInd,
      crLmtAmt: data?.entity?.arDtls?.[0]?.crLmtDtl?.crLmtAmt?.amt,
      closEndLneInd: data?.entity?.arDtls?.[0]?.crLmtDtl?.closEndLneInd
    }
    this.basicDetalsobj = this.setLOCBasicDtlsObj(data);
    // required only when a new account is entered  or new tab clicked on
    // account banner
    this.basicDetalsobj = this.setRoleAndWithoutRole(refreshDataOptn, data, this.basicDetalsobj);
    this._basicDetailsData = this.basicDetalsobj;
    return this.basicDetalsobj;
  }
  setLOCBasicDtlsObj(data) {
    let locLnBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].locLnBscDtlAndAdtTrl ? data.entity.arDtls[0].locLnBscDtlAndAdtTrl : '';
    if (locLnBscDtlAndAdtTrl) {
      this.basicDetalsobj.accountId = locLnBscDtlAndAdtTrl.arID ? locLnBscDtlAndAdtTrl.arID.arId : null;
      if (locLnBscDtlAndAdtTrl.arBscDtl) {
        this.basicDetalsobj.nickName = locLnBscDtlAndAdtTrl.arBscDtl.nickNme;
        this.basicDetalsobj.currencyCode = locLnBscDtlAndAdtTrl.arBscDtl.crncy ? locLnBscDtlAndAdtTrl.arBscDtl.crncy.cde : null;
        this.basicDetalsobj.originatingBranch = locLnBscDtlAndAdtTrl.arBscDtl.brnchOuId;
        this.basicDetalsobj.accountTitle = (locLnBscDtlAndAdtTrl.arBscDtl.acctTtls) ? locLnBscDtlAndAdtTrl.arBscDtl.acctTtls[0] : '';
        this.basicDetalsobj.currencyName = locLnBscDtlAndAdtTrl.arBscDtl.crncy ? locLnBscDtlAndAdtTrl.arBscDtl.crncy.nme : null;
      }
    }
    this.locAdtnlBscDtlAndAdtTrl(data);
    return this.basicDetalsobj;
  }
  locAdtnlBscDtlAndAdtTrl(data) {
    let adtnlBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].locLnBusDtl && data.entity.arDtls[0].locLnBusDtl.adtnlBscDtlAndAdtTrl && data.entity.arDtls[0].locLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetail ? data.entity.arDtls[0].locLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetail : '';
    if (adtnlBscDtlAndAdtTrl) {
      this.basicDetalsobj.industryCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.indusCde : null;
      this.basicDetalsobj.maturityDate = adtnlBscDtlAndAdtTrl.matDtl ? adtnlBscDtlAndAdtTrl.matDtl.matDte : null;
      this.basicDetalsobj.purposeCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.prpsCde : null;
      this.basicDetalsobj.servicingBranch = adtnlBscDtlAndAdtTrl.servBrnch;
      this.basicDetalsobj.stCde = adtnlBscDtlAndAdtTrl.stCde ? adtnlBscDtlAndAdtTrl.stCde : null;

    }
    this.locAdtnlBscDetail(data);
    return this.basicDetalsobj;
  }
  locAdtnlBscDetail(data) {
    this.basicDetalsobj.termType = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.matDtl?.lnTerms?.[0]?.lnTermTyp;
    this.basicDetalsobj.classCode = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.classCde;
    this.basicDetalsobj.riskRating = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.riskRtg;
    this.basicDetalsobj.riskRatingDate = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.riskRtgDte;
    this.basicDetalsobj.branchId = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.servBrnch;
    this.basicDetalsobj.managementClass = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.rptgDtl?.mgmtClass;
    this.basicDetalsobj.financedCharges = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.finacdChrg?.amt;
    this.basicDetalsobj.originalProceeds = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.origPrcd?.amt;
    this.basicDetalsobj.termValue = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.matDtl?.lnTerms?.[0]?.lnTermVal;
    return this.basicDetalsobj;
  }


  getSecLOCAccountBasicDetails(requestData: IArrangementInput): Observable<BasicDetailsData> {
    let basicdtls: any;
    basicdtls = this.getSecLOCArrangementDetails(requestData).pipe(
      map((data) => {
        this.dataShareService.setAppData('AR_RETRIEVE_RESPONSE', data);
        this._populateSecLOCBasicDetails(data, refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE);
        this.setAccountNumber(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.arId);
        return this._basicDetailsData;
      }));
    return basicdtls;
  }
  getSecLOCArrangementDetails(requestData: IArrangementInput): Observable<any> {
    requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls.map(element => element.arId.arId = element.arId && element.arId.arId ? element.arId.arId.toUpperCase() : null);
    if (requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.SECURED_LOC_AR_TYPE||LendingConstants.CONVENIENCEACCESSLINE) {
      return this.httpService.postHttpRequest(RETRIEVE_LOC_UNSECURED_LOAN_ACCOUNT_URI, requestData).pipe(catchError(async (error: HttpErrorResponse) =>  {
        this.errList.push(error);
        this.mgsDisplay();
      }));
    }

  }
  public _populateSecLOCBasicDetails(data, refreshDataOptn?: refreshBasicDetailOptns) {
    let lecaccountId;
    data.entity.arDtls[0] && data.entity.arDtls[0].locLnBscDtlAndAdtTrl && data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl && data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl.extAcctId && data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls ?
      data.entity.arDtls[0].locLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls.forEach((arid: any) => {
        if (arid.acctIdTyp == LendingConstants.LEGACYACCTNUM) {
          lecaccountId = arid.acctId;
        }
      }) : '';
    this.basicDetalsobj = {
      originalTermValue: data?.entity?.arDtls?.[0]?.locLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermVal,
      originalTermType: data?.entity?.arDtls?.[0]?.locLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermTyp,
      originalMaturityDate: data?.entity?.arDtls?.[0]?.locLnBusDtl?.matInfo?.origMatDte,
      customerCode: '',
      termVal: null,
      rateResetMaturityRecal: data?.entity?.arDtls?.[0]?.locLnBusDtl?.arPmtDtlAndAdtTrl?.rteChgDtl?.rteRstMatReCalcOpt,
      rateChangeFrequency: null,
      rateChangeEffctveDate: null,
      effectiveDate: data?.entity?.arDtls?.[0]?.locLnBscDtlAndAdtTrl?.arBscDtl?.effDte,
      primaryStatus: data?.entity?.arDtls?.[0]?.locLnBscDtlAndAdtTrl?.arBscDtl?.prmyStat,
      rolesTable: [],
      acctId: lecaccountId ? lecaccountId : '',
      cnvrsDte: data?.entity?.arDtls?.[0]?.locLnBscDtlAndAdtTrl?.cnvrsDte,
      advFromOrgntnInd: data?.entity?.arDtls?.[0]?.locLnBusDtl?.arDisbInfoAndAdtTrl?.advFromOrgntnInd,
      autoDisbAlwInd: data?.entity?.arDtls?.[0]?.locLnBusDtl?.arDisbInfoAndAdtTrl?.autoDisbAlwInd,
      crLmtAmt: data?.entity?.arDtls?.[0]?.crLmtDtl?.crLmtAmt?.amt,
      closEndLneInd: data?.entity?.arDtls?.[0]?.crLmtDtl?.closEndLneInd,
      collTyp: data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.collInfo?.collTyp,
      collCde: data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.collInfo?.collCde,
    }
    this.basicDetalsobj = this.setSecLOCBasicDtlsObj(data);
    // required only when a new account is entered  or new tab clicked on
    // account banner
    this.basicDetalsobj = this.setRoleAndWithoutRole(refreshDataOptn, data, this.basicDetalsobj);
    this._basicDetailsData = this.basicDetalsobj;
    return this.basicDetalsobj;
  }
  setSecLOCBasicDtlsObj(data) {
    let locLnBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].locLnBscDtlAndAdtTrl ? data.entity.arDtls[0].locLnBscDtlAndAdtTrl : '';
    if (locLnBscDtlAndAdtTrl) {
      this.basicDetalsobj.accountId = locLnBscDtlAndAdtTrl.arID ? locLnBscDtlAndAdtTrl.arID.arId : null;
      if (locLnBscDtlAndAdtTrl.arBscDtl) {
        this.basicDetalsobj.nickName = locLnBscDtlAndAdtTrl.arBscDtl.nickNme;
        this.basicDetalsobj.currencyCode = locLnBscDtlAndAdtTrl.arBscDtl.crncy ? locLnBscDtlAndAdtTrl.arBscDtl.crncy.cde : null;
        this.basicDetalsobj.originatingBranch = locLnBscDtlAndAdtTrl.arBscDtl.brnchOuId;
        this.basicDetalsobj.accountTitle = (locLnBscDtlAndAdtTrl.arBscDtl.acctTtls) ? locLnBscDtlAndAdtTrl.arBscDtl.acctTtls[0] : '';
        this.basicDetalsobj.currencyName = locLnBscDtlAndAdtTrl.arBscDtl.crncy ? locLnBscDtlAndAdtTrl.arBscDtl.crncy.nme : null;
      }
    }
    this.seclocAdtnlBscDtlAndAdtTrl(data);
    return this.basicDetalsobj;
  }
  seclocAdtnlBscDtlAndAdtTrl(data) {
    let adtnlBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].locLnBusDtl && data.entity.arDtls[0].locLnBusDtl.adtnlBscDtlAndAdtTrl && data.entity.arDtls[0].locLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetail ? data.entity.arDtls[0].locLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetail : '';
    if (adtnlBscDtlAndAdtTrl) {
      this.basicDetalsobj.industryCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.indusCde : null;
      this.basicDetalsobj.maturityDate = adtnlBscDtlAndAdtTrl.matDtl ? adtnlBscDtlAndAdtTrl.matDtl.matDte : null;
      this.basicDetalsobj.purposeCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.prpsCde : null;
      this.basicDetalsobj.servicingBranch = adtnlBscDtlAndAdtTrl.servBrnch;
      this.basicDetalsobj.stCde = adtnlBscDtlAndAdtTrl.stCde ? adtnlBscDtlAndAdtTrl.stCde : null;

    }
    this.seclocAdtnlBscDetail(data);
    return this.basicDetalsobj;
  }
  seclocAdtnlBscDetail(data) {
    this.basicDetalsobj.termType = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.matDtl?.lnTerms?.[0]?.lnTermTyp;
    this.basicDetalsobj.classCode = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.classCde;
    this.basicDetalsobj.riskRating = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.riskRtg;
    this.basicDetalsobj.riskRatingDate = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.riskRtgDte;
    this.basicDetalsobj.branchId = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.servBrnch;
    this.basicDetalsobj.managementClass = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.rptgDtl?.mgmtClass;
    this.basicDetalsobj.financedCharges = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.finacdChrg?.amt;
    this.basicDetalsobj.originalProceeds = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.origPrcd?.amt;
    this.basicDetalsobj.termValue = data?.entity?.arDtls?.[0]?.locLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetail?.matDtl?.lnTerms?.[0]?.lnTermVal;
    return this.basicDetalsobj;
  }

  getSecAccountBasicDetails(requestData: IArrangementInput): Observable<BasicDetailsData> {
    let basicdtls: any;
    basicdtls = this.getSecArrangementDetails(requestData).pipe(
      map((data) => {
        this.dataShareService.setAppData('AR_RETRIEVE_RESPONSE', data);
        this._populateSecBasicDetails(data, refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE);
        this.setAccountNumber(requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.arId);
        return this._basicDetailsData;
      }));
    return basicdtls;
  }

  getAccountBasicDetailObject(): BasicDetailsData {
    return this.accountBasicDetails;
  }

  setAccountBasicDetailObject(data: BasicDetailsData) {
    this.accountBasicDetails = data;
    return this.accountBasicDetails;
  }


  setBasicDtlsObj(data) {
    let unSecTermLnBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl ? data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl : '';
    if (unSecTermLnBscDtlAndAdtTrl) {
      this.basicDetalsobj.accountId = unSecTermLnBscDtlAndAdtTrl.arID ? unSecTermLnBscDtlAndAdtTrl.arID.arId : null;
      if (unSecTermLnBscDtlAndAdtTrl.arBscDtl) {
        this.basicDetalsobj.nickName = unSecTermLnBscDtlAndAdtTrl.arBscDtl.nickNme;
        this.basicDetalsobj.currencyCode = unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy ? unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy.cde : null;
        this.basicDetalsobj.originatingBranch = unSecTermLnBscDtlAndAdtTrl.arBscDtl.brnchOuId;
        this.basicDetalsobj.accountTitle = unSecTermLnBscDtlAndAdtTrl?.arBscDtl?.acctTtls?.[0];
        this.basicDetalsobj.currencyName = unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy ? unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy.nme : null;
      }
    }
    this.adtnlBscDtlAndAdtTrl(data);
    return this.basicDetalsobj;
  }

  setSecBasicDtlsObj(data) {
    let unSecTermLnBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl ? data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl : '';
    if (unSecTermLnBscDtlAndAdtTrl) {
      this.basicDetalsobj.accountId = unSecTermLnBscDtlAndAdtTrl.arID ? unSecTermLnBscDtlAndAdtTrl.arID.arId : null;
      if (unSecTermLnBscDtlAndAdtTrl.arBscDtl) {
        this.basicDetalsobj.nickName = unSecTermLnBscDtlAndAdtTrl.arBscDtl.nickNme;
        this.basicDetalsobj.currencyCode = unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy ? unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy.cde : null;
        this.basicDetalsobj.originatingBranch = unSecTermLnBscDtlAndAdtTrl.arBscDtl.brnchOuId;
        this.basicDetalsobj.accountTitle = unSecTermLnBscDtlAndAdtTrl.arBscDtl.acctTtls[0];
        this.basicDetalsobj.currencyName = unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy ? unSecTermLnBscDtlAndAdtTrl.arBscDtl.crncy.nme : null;
      }
    }
    this.adtnlSecBscDtlAndAdtTrl(data);
    return this.basicDetalsobj;
  }

  adtnlBscDtlAndAdtTrl(data) {
    let adtnlBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].unSecTermLnBusDtl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint ? data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint : '';
    if (adtnlBscDtlAndAdtTrl) {
      this.basicDetalsobj.industryCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.indusCde : null;
      this.basicDetalsobj.maturityDate = adtnlBscDtlAndAdtTrl.matDtl ? adtnlBscDtlAndAdtTrl.matDtl.matDte : null;
      this.basicDetalsobj.purposeCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.prpsCde : null;
      this.basicDetalsobj.servicingBranch = adtnlBscDtlAndAdtTrl.servBrnch;
      this.basicDetalsobj.stCde = adtnlBscDtlAndAdtTrl.stCde ? adtnlBscDtlAndAdtTrl.stCde : null;

    }
    this.adtnlBscDetailMaint(data);
    return this.basicDetalsobj;
  }

  adtnlBscDetailMaint(data) {
    this.basicDetalsobj.termType = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.matDtl?.lnTerms?.[0]?.lnTermTyp;
    this.basicDetalsobj.classCode = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.classCde;
    this.basicDetalsobj.riskRating = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.riskRtg;
    this.basicDetalsobj.riskRatingDate = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.riskRtgDte;
    this.basicDetalsobj.branchId = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.servBrnch;
    this.basicDetalsobj.managementClass = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.rptgDtl?.mgmtClass;
    this.basicDetalsobj.contractualAmount = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.cntrctalAmt?.amt;
    this.basicDetalsobj.financedCharges = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.finacdChrg?.amt;
    this.basicDetalsobj.originalProceeds = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.origPrcd?.amt;
    this.basicDetalsobj.termValue = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.matDtl?.lnTerms?.[0]?.lnTermVal;
    return this.basicDetalsobj;
  }

  adtnlSecBscDtlAndAdtTrl(data) {
    let adtnlBscDtlAndAdtTrl = data.entity.arDtls[0] && data.entity.arDtls[0].unSecTermLnBusDtl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint ? data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint : '';
    if (adtnlBscDtlAndAdtTrl) {
      this.basicDetalsobj.industryCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.indusCde : null;
      this.basicDetalsobj.maturityDate = adtnlBscDtlAndAdtTrl.matDtl ? adtnlBscDtlAndAdtTrl.matDtl.matDte : null;
      this.basicDetalsobj.purposeCode = adtnlBscDtlAndAdtTrl.rptgDtl ? adtnlBscDtlAndAdtTrl.rptgDtl.prpsCde : null;
      this.basicDetalsobj.servicingBranch = adtnlBscDtlAndAdtTrl.servBrnch;
      this.basicDetalsobj.stCde = adtnlBscDtlAndAdtTrl.stCde ? adtnlBscDtlAndAdtTrl.stCde : null;

    }
    this.adtnlSecBscDetailMaint(data);
    return this.basicDetalsobj;
  }

  adtnlSecBscDetailMaint(data) {
    this.basicDetalsobj.termType = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.matDtl?.lnTerm?.[0]?.lnTermTyp;
    this.basicDetalsobj.classCode = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.classCde;
    this.basicDetalsobj.riskRating = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.riskRtg;
    this.basicDetalsobj.riskRatingDate = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.riskRtgDte;
    this.basicDetalsobj.branchId = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.servBrnch;
    this.basicDetalsobj.managementClass = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.rptgDtl?.mgmtClass;
    this.basicDetalsobj.contractualAmount = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.cntrctalAmt?.amt;
    this.basicDetalsobj.financedCharges = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.finacdChrg?.amt;
    this.basicDetalsobj.originalProceeds = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.origPrcd?.amt;
    this.basicDetalsobj.termValue = data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.matDtl?.lnTerm?.[0]?.lnTermVal;
    return this.basicDetalsobj;
  }


  public _populateBasicDetails(data, refreshDataOptn?: refreshBasicDetailOptns) {
    let lecaccountId;
    data.entity.arDtls[0] && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl.extAcctId && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls ?
      data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls.forEach((arid: any) => {
        if (arid.acctIdTyp == LendingConstants.LEGACYACCTNUM) {
          lecaccountId = arid.acctId;
        }
      }) : '';
    this.basicDetalsobj = {
      fedCallCode: data.entity.arDtls[0] && data.entity.arDtls[0].usSpcfcLnArDtlAndAdtTrl ? data.entity.arDtls[0].usSpcfcLnArDtlAndAdtTrl.fedCallCde : null,
      originalTermValue: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermVal,
      originalTermType: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermTyp,
      originalMaturityDate: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.matInfo?.origMatDte,
      customerCode: '',
      termVal: null,
      overPmntReCalculationOptn: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.arPmtDtlAndAdtTrl?.prePmtDtl?.ovrpmtMatRecalcOpt,
      rateResetMaturityRecal: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.arPmtDtlAndAdtTrl?.rteChgDtl?.rteRstMatReCalcOpt,
      rateChangeFrequency: null,
      rateChangeEffctveDate: null,
      effectiveDate: data?.entity?.arDtls?.[0]?.unSecTermLnBscDtlAndAdtTrl?.arBscDtl?.effDte,
      primaryStatus: data?.entity?.arDtls?.[0]?.unSecTermLnBscDtlAndAdtTrl?.arBscDtl?.prmyStat,
      rolesTable: [],
      acctId: lecaccountId ? lecaccountId : '',
      cnvrsDte: data?.entity?.arDtls?.[0]?.unSecTermLnBscDtlAndAdtTrl?.cnvrsDte,
      strtDte: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.strtDte,
      endDte: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.endDte,
      sameAsCashStat: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashStat,
      sameAsCashTermTyp: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashTerm?.termTyp,
      sameAsCashTermVal: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashTerm?.termVal,
      sameAsCashEndDteOpt: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashTerm?.endDteOpt,
      canceltnDte: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.cncltnDte,
      canceltnResn: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.cncltnResn,
      indirLnInd: data?.entity?.arDtls?.[0]?.indirLnInd
    }
    this.basicDetalsobj = this.setBasicDtlsObj(data);
    // required only when a new account is entered  or new tab clicked on
    // account banner
    this.basicDetalsobj = this.setRoleAndWithoutRole(refreshDataOptn, data, this.basicDetalsobj);
    this._basicDetailsData = this.basicDetalsobj;
    return this.basicDetalsobj;
  }

  setRoleAndWithoutRole(refreshDataOptn, data, basicDetalsobj) {
    // required only when a new account is entered  or new tab clicked on
    // account banner
    if (refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE) {
      data.entity.arDtls[0].ipRoleAndAdtTrls.forEach((row) => {
        let roleInfo: RoleInfo = {} as RoleInfo;
        roleInfo = this.populateRoleInfo(row);
        basicDetalsobj.rolesTable.push(roleInfo);
      });
      basicDetalsobj = this.setTermValue(data, basicDetalsobj);
      this._rolesTable = basicDetalsobj.rolesTable;
    }
    // will be invoked from EDIT BASIC DETAILS
    if (refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITHOUT_ROLETABLE) {

      basicDetalsobj.rolesTable = this._rolesTable;
    }
    return basicDetalsobj;
  }

  setTermValueInfo(termTypeValInfo, data) {
    const termValArr = data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint.matDtl.lnTerms ? data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint.matDtl.lnTerms : data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint.matDtl.lnTerm;
    termValArr.forEach(element => {
      if (element.lnTermTyp === 'Years') {
        termTypeValInfo.isYearAttr = true;
        termTypeValInfo.yearVal = element.lnTermVal;
      }
      if (element.lnTermTyp === 'Months') {
        termTypeValInfo.isMonthAttr = true;
        termTypeValInfo.monthVal = element.lnTermVal;
      }
      if (element.lnTermTyp === 'Days') {
        termTypeValInfo.isDayAttr = true;
        termTypeValInfo.dayVal = element.lnTermVal;
      }
    });
    return termTypeValInfo;
  }

  setSecTermValueInfo(termTypeValInfo, data) {
    const termValArr = data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint.matDtl.lnTerm;
    termValArr.forEach(element => {
      if (element.lnTermTyp === 'Years') {
        termTypeValInfo.isYearAttr = true;
        termTypeValInfo.yearVal = element.lnTermVal;
      }
      if (element.lnTermTyp === 'Months') {
        termTypeValInfo.isMonthAttr = true;
        termTypeValInfo.monthVal = element.lnTermVal;
      }
      if (element.lnTermTyp === 'Days') {
        termTypeValInfo.isDayAttr = true;
        termTypeValInfo.dayVal = element.lnTermVal;
      }
    });
    return termTypeValInfo;
  }

  setTermValue(data, basicDetalsobj) {
    if (data.entity.arDtls[0].unSecTermLnBusDtl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint.matDtl) {
      let termTypeValInfo: TermTypeValBscDtls = {} as TermTypeValBscDtls;
      termTypeValInfo = this.setTermValueInfo(termTypeValInfo, data);
      if (termTypeValInfo.isYearAttr) {
        this.basicDetalsobj = this.setisYearAttr(termTypeValInfo, basicDetalsobj);
      }
      if (!termTypeValInfo.isYearAttr) {
        this.basicDetalsobj = this.setisNonYearAttr(termTypeValInfo, basicDetalsobj);
      }
    }
    return basicDetalsobj;
  }

  setSecTermValue(data, basicDetalsobj) {
    if (data.entity.arDtls[0].unSecTermLnBusDtl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl && data.entity.arDtls[0].unSecTermLnBusDtl.adtnlBscDtlAndAdtTrl.adtnlBscDetailMaint.matDtl) {
      let termTypeValInfo: TermTypeValBscDtls = {} as TermTypeValBscDtls;
      termTypeValInfo = this.setSecTermValueInfo(termTypeValInfo, data);
      if (termTypeValInfo.isYearAttr) {
        this.basicDetalsobj = this.setisYearAttr(termTypeValInfo, basicDetalsobj);
      }
      if (!termTypeValInfo.isYearAttr) {
        this.basicDetalsobj = this.setisNonYearAttr(termTypeValInfo, basicDetalsobj);
      }
    }
    return basicDetalsobj;
  }

  setisNonYearAttr(termTypeValInfo, basicDetalsobj) {
    if (termTypeValInfo.isMonthAttr && termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.monthVal + ' ' + termTypeValInfo.dayVal;
    }
    if (!termTypeValInfo.isMonthAttr && termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.dayVal;
    }
    //new change
    if (termTypeValInfo.isMonthAttr && !termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.monthVal;
    }
    return basicDetalsobj;
  }

  setisYearAttr(termTypeValInfo, basicDetalsobj) {
    if (termTypeValInfo.isMonthAttr && termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.yearVal + ' ' + termTypeValInfo.monthVal + ' ' + termTypeValInfo.dayVal;
    }
    if (termTypeValInfo.isMonthAttr && !termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.yearVal + ' ' + termTypeValInfo.monthVal;
    }
    if (!termTypeValInfo.isMonthAttr && termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.yearVal + ' ' + termTypeValInfo.dayVal;
    }
    if (!termTypeValInfo.isMonthAttr && !termTypeValInfo.isDayAttr) {
      basicDetalsobj.termValue = termTypeValInfo.yearVal;
    }
    return basicDetalsobj;
  }
  public _populateSecBasicDetails(data, refreshDataOptn?: refreshBasicDetailOptns): BasicDetailsData {
    let lecaccountId;
    lecaccountId = this.getLegacyAccNum(data);
    this.basicDetalsobj = {
      fedCallCode: data.entity.arDtls[0] && data.entity.arDtls[0].usSpcfcLnArDtlAndAdtTrl ? data.entity.arDtls[0].usSpcfcLnArDtlAndAdtTrl.fedCallCde : null,
      originalTermValue: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermVal,
      originalTermType: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.matInfo?.origLnTerms?.[0]?.lnTermTyp,
      originalMaturityDate: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.matInfo?.origMatDte,
      customerCode: '',
      termVal: null,
      overPmntReCalculationOptn: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.arPmtDtlAndAdtTrl?.prePmtDtl?.ovrpmtMatRecalcOpt,
      rateResetMaturityRecal: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.arPmtDtlAndAdtTrl?.rteChgDtl?.rteRstMatReCalcOpt,
      rateChangeFrequency: null,
      rateChangeEffctveDate: null,
      effectiveDate: data?.entity?.arDtls?.[0]?.unSecTermLnBscDtlAndAdtTrl?.arBscDtl?.effDte,
      primaryStatus: data?.entity?.arDtls?.[0]?.unSecTermLnBscDtlAndAdtTrl?.arBscDtl?.prmyStat,
      rolesTable: [],
      collTyp: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.collInfo?.collTyp,
      collCde: data?.entity?.arDtls?.[0]?.unSecTermLnBusDtl?.adtnlBscDtlAndAdtTrl?.adtnlBscDetailMaint?.collInfo?.collCde,
      cnvrsDte: data?.entity?.arDtls?.[0]?.unSecTermLnBscDtlAndAdtTrl?.conversionDte,
      acctId: lecaccountId ? lecaccountId : '',
      strtDte: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.strtDte,
      endDte: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.endDte,
      sameAsCashStat: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashStat,
      sameAsCashTermTyp: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashTerm?.termTyp,
      sameAsCashTermVal: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashTerm?.termVal,
      sameAsCashEndDteOpt: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.sameAsCashTerm?.endDteOpt,
      canceltnDte: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.cncltnDte,
      canceltnResn: data?.entity?.arDtls?.[0]?.sameAsCashDtl?.cncltnResn,
      indirLnInd: data?.entity?.arDtls?.[0]?.indirLnInd
    }
    this.basicDetalsobj = this.setSecBasicDtlsObj(data);

    // required only when a new account is entered  or new tab clicked on
    // account banner
    if (refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITH_ROLETABLE) {
      data.entity.arDtls[0].ipRoleAndAdtTrls.forEach((row) => {
        let roleInfo: RoleInfo = {} as RoleInfo;
        roleInfo = this.populateRoleInfo(row);
        this.basicDetalsobj.rolesTable.push(roleInfo);
      });

      this.setSecTermValue(data, this.basicDetalsobj);

      this._rolesTable = this.basicDetalsobj.rolesTable;
    }
    // will be invoked from EDIT BASIC DETAILS
    if (refreshDataOptn === refreshBasicDetailOptns.REFRESH_DATA_WITHOUT_ROLETABLE) {

      this.basicDetalsobj.rolesTable = this._rolesTable;
    }

    this._basicDetailsData = this.basicDetalsobj;
    return this.basicDetalsobj;
  }

  populateRoleInfo(row): RoleInfo {
    let custName: string;
    this.customerService.getIpNameFromCustId({ ipRqstKey: { ecIpId: row.ipRlt.ipId.ipId } }).pipe(
      map((customerdata: any) => {
        custName = customerdata.entity && customerdata.entity.nmes && customerdata.entity.nmes.length > 0 ? customerdata.entity.nmes[0].nmeLne : "";
      }));
    return {
      customerNumber: row.ipRlt.ipId.ipId,
      customerName: custName,
      roleType: row.ipRlt.arIpRlt.rltCde == 'Beneficent' ?  (row.ipRlt.arIpRlt.roleTyp.toUpperCase() + "-" + row.ipRlt.arIpRlt.rltCde.toUpperCase()) : (row.ipRlt.arIpRlt.roleTyp.toUpperCase() + "-" + row.ipRlt.arIpRlt.rltCde.toUpperCase()),
      roleValue: row.ipRlt.arIpRlt.rltCde,
      percentage: row.ipRlt.arIpRlt.rltPrcntg,
      startDate: row.ipRlt.arIpRlt.strtDte ? row.ipRlt.arIpRlt.strtDte : null
    }
  }
  getLegacyAccNum(data) {
    let lecaccountId;
    data.entity.arDtls[0] && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl.extAcctId && data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls ?
      data.entity.arDtls[0].unSecTermLnBscDtlAndAdtTrl.arBscDtl.extAcctId.adtnlAcctDtls.forEach((arid: any) => {
        if (arid.acctIdTyp == LendingConstants.LEGACYACCTNUM) {
          lecaccountId = arid.acctId;
        }
      }) : '';
    return lecaccountId;
  }
  getAccountNumber() {
    return this.accountID;
  }
  setAccountNumber(accountnum) {
    this.accountID = accountnum;
  }
  setRoleTableCustName(data) {
    for (let j = 0; j < data.length; j++) {
      if (this._rolesTable[j] && data[j]) {
        this._rolesTable[j].customerName = data[j].customerName;
      }
    }
  }
  getRoleTableData() {
    return this._rolesTable;
  }
  setCostCenterDetails(code: any) {
    this._basicDetailsData.costCenter = code;
  }
  getCostCenterDetails() {
    return this._basicDetailsData.costCenter;
  }
  setRateResetMaturityDetails(rateResetMaturity: string,
    rateChangeEffctveDate: string,
    rateChangeValue: number,
    rateChangeFrequency: string) {
    this._basicDetailsData.rateResetMaturityRecal = rateResetMaturity;
    this._basicDetailsData.rateChangeEffctveDate = rateChangeEffctveDate;
    this._basicDetailsData.rateChangeValue = rateChangeValue;
    this._basicDetailsData.rateChangeFrequency = rateChangeFrequency;
  }
  getArrangementDetails(requestData: IArrangementInput): Observable<any> {
    requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls.map(element => element.arId.arId = element.arId && element.arId.arId ? element.arId.arId.toUpperCase() : null);
    if (requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.SECURED_AR_TYPE) {
      return this.httpService.postHttpRequest(RETRIEVE_SECURED_LOAN_ACCOUNT_URI, requestData).pipe(catchError(async (error: HttpErrorResponse) =>  {
        this.errList.push(error);
        this.mgsDisplay();
      }));
    } else if (requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.UNSECURED_AR_TYPE) {
      return this.httpService.postHttpRequest(ACCOUNT_DETAIL_RETRIEVE_URI, requestData).pipe(catchError(async (error: HttpErrorResponse) =>  {
        this.errList.push(error);
        this.mgsDisplay();
      }));
    } else if (requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.UNSECURED_BNPL_AR_TYPE) {
      return this.httpService.postHttpRequest(RETRIEVE_BNPL_LOAN_ACCOUNT_URI, requestData).pipe(catchError(async (error: HttpErrorResponse) =>  {
        this.errList.push(error);
        this.mgsDisplay();
      }));
    } else if (requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls[0].arId.entprsArTyp === LendingConstants.SECURED_LOC_AR_TYPE) {
      return this.accountdetailsService.transformLocSecResp(requestData);
    } else {
      return this.accountdetailsService.transformLocUnsecResp(requestData);
    }

  }
  getSecArrangementDetails(requestData: IArrangementInput): Observable<any> {
    requestData.lnArRtrvCrtra.arRtrvCrtra.arKeyDtls.map(element => element.arId.arId = element.arId && element.arId.arId ? element.arId.arId.toUpperCase() : null);
    return this.httpService.postHttpRequest(RETRIEVE_SECURED_LOAN_ACCOUNT_URI, requestData).pipe(catchError(async (error: HttpErrorResponse) =>  {
      this.errList.push(error);
      this.mgsDisplay();
    }));
  }


  mgsDisplay(){
    if (this.errList.length > 0) {
      this.msgHandlerService.clearMsgData();
      this.msgHandlerService.setMsgData('error', this.errList);
      this.errList = [];
  }
  }
}
