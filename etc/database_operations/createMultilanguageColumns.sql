alter table EVALUATION_METHOD change column EVALUATION_ELEMENTS OLD_EVALUATION_ELEMENTS text;
update EVALUATION_METHOD set OLD_EVALUATION_ELEMENTS = '__xpto__' where OLD_EVALUATION_ELEMENTS is null or OLD_EVALUATION_ELEMENTS = '';
update EVALUATION_METHOD set EVALUATION_ELEMENTS_EN = '__xpto__' where EVALUATION_ELEMENTS_EN is null or EVALUATION_ELEMENTS_EN = '';
alter table EVALUATION_METHOD add column EVALUATION_ELEMENTS longtext;
update EVALUATION_METHOD set EVALUATION_METHOD.EVALUATION_ELEMENTS = concat('pt', length(replace(EVALUATION_METHOD.OLD_EVALUATION_ELEMENTS, "__xpto__", "")), ':', replace(EVALUATION_METHOD.OLD_EVALUATION_ELEMENTS, "__xpto__", ""), 'en', length(replace(EVALUATION_METHOD.EVALUATION_ELEMENTS_EN, "__xpto__", "")), ':', replace(EVALUATION_METHOD.EVALUATION_ELEMENTS_EN, "__xpto__", "")); 
update EVALUATION_METHOD set EVALUATION_ELEMENTS = NULL WHERE EVALUATION_ELEMENTS = "pt0:en0:";
update EVALUATION_METHOD set EVALUATION_ELEMENTS = replace(EVALUATION_METHOD.EVALUATION_ELEMENTS, "pt0:", "");
update EVALUATION_METHOD set EVALUATION_ELEMENTS = replace(EVALUATION_METHOD.EVALUATION_ELEMENTS, "en0:", "");
