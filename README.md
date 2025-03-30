It will help to learn and go through Java Concepts :)

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
