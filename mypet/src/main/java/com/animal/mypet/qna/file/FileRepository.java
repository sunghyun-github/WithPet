package com.animal.mypet.qna.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("qnaFileRepository")
public interface FileRepository extends JpaRepository<File, Integer> {

}
