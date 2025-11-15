package vn.hackathon.backend.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MockDataConstants {
  public final String MOCK_ACADEMIC_TRANSPORT =
"""
[
{"student_id":"22110221","student_name":"Tran Thi Binh","total_subjects":138,
"terms":[
{"term":"2223-1","total_subjects":19,"passed_subjects":18,"failed_subjects":1},
{"term":"2223-2","total_subjects":21,"passed_subjects":21,"failed_subjects":0},
{"term":"2324-1","total_subjects":25,"passed_subjects":24,"failed_subjects":1},
{"term":"2324-2","total_subjects":21,"passed_subjects":21,"failed_subjects":0},
{"term":"2425-1","total_subjects":28,"passed_subjects":27,"failed_subjects":1},
{"term":"2425-2","total_subjects":24,"passed_subjects":24,"failed_subjects":0}
]},
{"student_id":"22110222","student_name":"Le Hoang Phuc","total_subjects":140,
"terms":[
{"term":"2223-1","total_subjects":20,"passed_subjects":20,"failed_subjects":0},
{"term":"2223-2","total_subjects":20,"passed_subjects":20,"failed_subjects":0},
{"term":"2324-1","total_subjects":26,"passed_subjects":24,"failed_subjects":2},
{"term":"2324-2","total_subjects":22,"passed_subjects":21,"failed_subjects":1},
{"term":"2425-1","total_subjects":27,"passed_subjects":27,"failed_subjects":0},
{"term":"2425-2","total_subjects":25,"passed_subjects":24,"failed_subjects":1}
]},
{"student_id":"22110224","student_name":"Vo Thi My Duyen","total_subjects":144,
"terms":[
{"term":"2223-1","total_subjects":21,"passed_subjects":20,"failed_subjects":1},
{"term":"2223-2","total_subjects":22,"passed_subjects":21,"failed_subjects":1},
{"term":"2324-1","total_subjects":27,"passed_subjects":27,"failed_subjects":0},
{"term":"2324-2","total_subjects":23,"passed_subjects":22,"failed_subjects":1},
{"term":"2425-1","total_subjects":26,"passed_subjects":26,"failed_subjects":0},
{"term":"2425-2","total_subjects":25,"passed_subjects":25,"failed_subjects":0}
]}
]""";
}
