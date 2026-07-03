package com.library.libraryapi.service;

import com.library.libraryapi.dto.MemberRequestDTO;
import com.library.libraryapi.dto.MemberResponseDTO;
import com.library.libraryapi.exception.BusinessException;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.model.Member;
import com.library.libraryapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDTO create(MemberRequestDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um membro cadastrado com o e-mail " + dto.getEmail());
        }

        Member member = Member.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();

        return toResponseDTO(memberRepository.save(member));
    }

    public List<MemberResponseDTO> findAll() {
        return memberRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public MemberResponseDTO findById(Long id) {
        return toResponseDTO(getMemberOrThrow(id));
    }

    public MemberResponseDTO update(Long id, MemberRequestDTO dto) {
        Member member = getMemberOrThrow(id);
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        return toResponseDTO(memberRepository.save(member));
    }

    public void delete(Long id) {
        Member member = getMemberOrThrow(id);
        memberRepository.delete(member);
    }

    public Member getMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado com id " + id));
    }

    private MemberResponseDTO toResponseDTO(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }
}
