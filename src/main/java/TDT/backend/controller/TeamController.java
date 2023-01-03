package TDT.backend.controller;

import TDT.backend.dto.team.StudyJoinReqDto;
import TDT.backend.dto.team.StudyListResponseDto;
import TDT.backend.dto.team.StudyRequestDto;
import TDT.backend.dto.team.StudyResponseDto;
import TDT.backend.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/{category}")
    public ResponseEntity<Page<StudyListResponseDto>> getAllStudy(@PathVariable String category,
                                                               @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(teamService.getAllStudy(category, pageable));
    }
    @PostMapping("/posting-study")
    public ResponseEntity<Long> addStudy(@RequestBody StudyRequestDto params) {
        return ResponseEntity.ok(teamService.addTeam(params));
    }

    @PostMapping("/join")
    public ResponseEntity joinStudy(@RequestBody StudyJoinReqDto params) {
        teamService.joinTeam(params);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyResponseDto> getStudy(@PathVariable("id") Long studyId,
                                                     @RequestParam Long memberId,
                                                     @RequestParam String category) {
        /**Todo
         * memberId를 통해 내 스터디인지 확인
         */
        return ResponseEntity.ok(teamService.getStudy(category, studyId));
    }

//    @PostMapping("/{studyId}")
//    public ResponseEntity<Boolean> updateStudy(@PathVariable("sutdyId") Long studyId,
//                                               @RequestParam Long id) {
//        return ResponseEntity.ok(teamService.updateStudy(studyId, id));
//    }
    @DeleteMapping("/")
    public ResponseEntity<Boolean> deleteStudy(@RequestParam Long studyId,
                                               @RequestParam Long id) {
        return ResponseEntity.ok(teamService.deleteStudy(studyId, id));
    }
}
