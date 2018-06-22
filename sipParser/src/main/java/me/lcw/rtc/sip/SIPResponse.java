package me.lcw.rtc.sip;

public enum SIPResponse {
 Trying(100, "Trying"),
 Ringing(180, "Ringing"),
 CallIsBeingForwarded(181, "Call is Being Forwarded"),
 Queued(182, "Queued"),
 SessionInProgress(183, ""),
 EarlyDialogTerminated(199, ""),

 OK(200, "OK"),
 Accept(202, "Accept"),
 NoNotification(203, "No Notification"),

 MultipleChoices(300, "Multiple Choices"),
 MovedPermanently(301, "Moved Permanently"),
 MovedTemporarily(302, "Moved Temporarily"),
 UseProxy(305, "Use Proxy"),
 AlternativeService(380, "Alternative Service"),

 BadRequest(400, "Bad Request"),
 Unauthorized(401, "Unauthorized"),
 PaymentRequired(402, "Payment Required"),
 Forbidden(403, "Forbidden"),
 NotFound(404, "Not Found"),
 MethodNotAllowed(405, "Method Not Allowed"),
 NotAcceptable(406, "Not Acceptable"),
 ProxyAuthenticationRequired(407, "Proxy Authentication Required"),
 RequestTimeout(408, "Request Timeout"),
 Conflict(409, "Conflict"),
 Gone(410, "Gone"),
 LengthRequired(411, "Length Required"),
 ConditionalRequestFailed(412, "Conditional Request Failed"),
 RequestEntityTooLarge(413, "Request Entity Too Large"),
 RequestURITooLong(414, "Request-URI Too Long"),
 UnsupportedMediaType(415, "Unsupported Media Type"),
 UnsupportedURIScheme(416, "Unsupported URI Scheme"),
 UnknownResourcePriority(417, "Unknown Resource-Priority"),
 BadExtension(420, "Bad Extension"),
 ExtensionRequired(421, "Extension Required"),
 SessionIntervalTooSmall(422, "Session Interval Too Small"),
 IntervalTooBrief(423, "Interval Too Brief"),
 BadLocationInformation(424, "Bad Location Information"),
 UseIdentityHeader(428, "Use Identity Header"),
 ProvideReferrerIdentity(429, "Provide Referrer Identity"),
 FlowFailed(430, "Flow Failed"),
 AnonymityDisallowed(433, "Anonymity Disallowed"),
 BadIdentityInfo(436, "Bad Identity-Info"),
 UnsupportedCertificate(437, "Unsupported Certificate"),
 InvalidIdentityHeader(438, "Invalid Identity Header"),
 FirstHopLacksOutboundSupport(439, "First Hop Lacks Outbound Support"),

 MaxBreadthExceeded(440, "Max-Breadth Exceeded"),
 BadInfoPackage(469, "Bad Info Package"),
 ConsentNeeded(470, "Consent Needed"),
 TemporarilyUnavailable(480, "Temporarily Unavailable"),
 CallTransactionDoesNotExist(481, "Call/Transaction Does Not Exist"),
 LoopDetected(482, "Loop Detected."),
 TooManyHops(483, "Too Many Hops"),
 AddressIncomplete(484, "Address Incomplete"),
 Ambiguous(485, "Ambiguous"),
 BusyHere(486, "Busy Here"),
 RequestTerminated(487, "Request Terminated"),
 NotAcceptableHere(488, "Not Acceptable Here"),
 BadEvent(489, "Bad Event"),
 RequestPending(491, "Request Pending"),
 Undecipherable(493, "Undecipherable"),
 SecurityAgreementRequired(494, "Security Agreement Required"),

 ServerInternalError(500, "Server Internal Error"),
 NotImplemented(501, "Not Implemented"),
 BadGateway(502, "Bad Gateway"),
 ServiceUnavailable(503, "Service Unavailable"),
 ServerTimeout(504, "Server Time-out"),
 VersionNotSupported(505, "Version Not Supported"),
 MessageTooLarge(513, "Message Too Large"),
 PreconditionFailure(580, "Precondition Failure"),

 BusyEverywhere(600, "Busy Everywhere"),
 Decline(603, "Decline"),
 DoesNotExistAnywhere(604, "Does Not Exist Anywhere"),
 NotAcceptableGlobal(606, "Not Acceptable"),
 Unwanted(607, "Unwanted");
  
  
  private final int code;
  private final String text;
  
  SIPResponse(int code, String text) {
    this.code = code;
    this.text = text;
  }
  
  public int getCode() {
    return code;
  }
  
  @Override
  public String toString() {
    return code+" "+text;
  }
  
  public static SIPResponse fromStringCode(String oc) throws SipParsingError {
    String tmp = oc.trim().substring(0, 3);
    try {
      int code = Integer.parseInt(tmp);
      for(SIPResponse sr: SIPResponse.values()) {
        if(sr.getCode() == code) {
          return sr;
        }
      }
    } catch(Exception e) {
      throw new SipParsingError(e);  
    }
    throw new SipParsingError();
  }
}