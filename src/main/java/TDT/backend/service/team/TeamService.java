package TDT.backend.service.team;

import TDT.backend.dto.comment.CommentRes;
import TDT.backend.dto.member.MemberDto;
import TDT.backend.dto.schedule.ScheduleDto;
import TDT.backend.dto.team.StudyListResponseDto;
import TDT.backend.dto.team.StudyRequestDto;
import TDT.backend.dto.team.StudyResponseDto;
import TDT.backend.entity.*;
import TDT.backend.exception.BusinessException;
import TDT.backend.exception.ExceptionCode;
import TDT.backend.repository.comment.CommentRepository;
import TDT.backend.repository.memberSchedule.MemberScheduleRepository;
import TDT.backend.repository.notice.NoticeRepository;
import TDT.backend.repository.schedule.ScheduleRepository;
import TDT.backend.repository.team.TeamRepository;
import TDT.backend.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public Page<StudyListResponseDto> getAllStudy(String category, Pageable pageable) {
        return teamRepository.findAllByCategoryAndPageable(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StudyListResponseDto> getAllKindOfStudy(Pageable pageable) {
        return teamRepository.findAllByPageable(pageable);
    }


    public Long addTeam(final StudyRequestDto params, Member member) {
        Team team = Team.fromStudyRequestDto(params, member);
        TeamMember teamMember = TeamMember.of(member, team);
        teamRepository.save(team);
        return teamMemberRepository.save(teamMember).getTeam().getId();
    }


    /**
     * 1. 팀 등록시
     * Todo Notice엔티티에 값이랑 카테고리 넣기
     */
    public void joinTeam(Long studyId, Member member) {
        if (teamMemberRepository.findByMemberIdAndTeamId(member.getId(), studyId).isEmpty()) {
            TeamMember leader = teamMemberRepository.findLeaderByTeamId(studyId);
            teamMemberRepository.save(TeamMember.joinRequest(member, leader.getTeam()));
            noticeRepository.save(Notice.of(leader.getMember(), NoticeCategory.studyJoin));
        } else throw new BusinessException(ExceptionCode.ALREADY_JOIN_REQUEST);
    }

    public boolean acceptJoinStudy(Long studyId, Long memberId) {
        TeamMember teamMember = teamMemberRepository.findByMemberIdAndTeamId(memberId, studyId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_EXISTS));
        if (teamMember.getStatus().equals(MemberStatus.guest)) {
            teamMember.joinAccept();
            return true;
        } else return false;
    }

    @Transactional(readOnly = true)
    public StudyResponseDto getStudy(Long studyId) {

        TeamMember writer = teamMemberRepository.findLeaderByTeamId(studyId);

        List<ScheduleDto> schedules = scheduleRepository.findScheduleByStudyId(studyId);

        List<MemberDto> checkedMembers = memberScheduleRepository.findMembersByStudyId(studyId);

        List<CommentRes> comments = commentRepository.findCommentsByPostIdOrStudyId(null, studyId);

        StudyResponseDto response = StudyResponseDto.of(studyId, writer, schedules, checkedMembers, comments);

        return response;
    }

    public boolean deleteStudy(Long studyId, Member member) {

        Team team = teamRepository.findById(studyId).orElseThrow(
                () -> new BusinessException(ExceptionCode.TEAM_NOT_EXISTS));

        TeamMember leader = teamMemberRepository.findLeaderByTeamId(studyId);

        if (member.getId().equals(leader.getMember().getId())) {
            teamRepository.delete(team);
        } else {
            throw new BusinessException(ExceptionCode.UNAUTHORIZED_ERROR);
        }

        return true;
    }
    /**
     * Todo 권한주기
     *
     */

//    public Boolean updateStudy(Long studyId, Long id) {
//        /**
//         *
//         *
//         */
//    }
}
