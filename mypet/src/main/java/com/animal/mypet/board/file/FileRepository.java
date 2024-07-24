package com.animal.mypet.board.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("boardFileRepository")
public interface FileRepository extends JpaRepository<File, Long> {

}
